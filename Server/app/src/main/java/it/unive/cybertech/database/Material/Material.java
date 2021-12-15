package it.unive.cybertech.database.Material;

import static it.unive.cybertech.database.Database.addToCollection;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Geoquerable;
import it.unive.cybertech.database.Material.Exception.NoMaterialFoundException;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;
import it.unive.cybertech.database.Profile.User;

public class Material extends Geoquerable {
    private final static String table = "material";
    private String id;
    private DocumentReference owner;
    private DocumentReference renter;
    private boolean isRent;
    private String title;
    private String description;
    private String photo;
    private GeoPoint location;
    private String geohash;
    private Timestamp expiryDate;
    private DocumentReference type;

    public Material() {}

    private Material(String id, DocumentReference owner, String title,
                    String description, String photo, DocumentReference renter, boolean isRent,
                    GeoPoint location, String geohash, DocumentReference type, Timestamp expiryDate) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.renter = renter;
        this.isRent = isRent;
        this.location = location;
        this.geohash = geohash;
        this.type = type;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    private void setOwner(DocumentReference owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    private void setPhoto(String photo) {
        this.photo = photo;
    }

    public DocumentReference getType() {
        return type;
    }

    private void setType(DocumentReference type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public DocumentReference getRenter() {
        return renter;
    }

    private void setRenter(DocumentReference renter) {
        this.renter = renter;
    }

    public GeoPoint getLocation() {
        return location;
    }

    private void setLocation(GeoPoint location) {
        this.location = location;
    }

    private String getGeohash() {
        return geohash;
    }

    private void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    private void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public static Material createMaterial(@NonNull User owner, String title, String description, String photo,
                                          @NonNull Type type, double latitude, double longitude, Date date)
            throws ExecutionException, InterruptedException {

        DocumentReference docPro = getReference("users", owner.getId());
        DocumentReference docType = getReference("type", type.getID());
        String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
        Timestamp timestamp = new Timestamp(date);
        GeoPoint location = new GeoPoint(latitude, longitude);

        Map<String, Object> myMaterial = new HashMap<>();          //create "table"
        myMaterial.put("title", title);
        myMaterial.put("owner", docPro);
        myMaterial.put("isRent", false);
        myMaterial.put("description", description);
        myMaterial.put("photo", photo);
        myMaterial.put("expiryDate", timestamp);
        myMaterial.put("type", docType);
        myMaterial.put("location", location);
        myMaterial.put("geohash", geohash);

        DocumentReference addedDocRef = addToCollection(table, myMaterial);

        return new Material(addedDocRef.getId(), docPro, title, description,
                photo, null, false,  location, geohash, docType, timestamp);
    }

    public static Material getMaterialById(@NonNull String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Material material = null;

        if (document.exists()){
            material = document.toObject(Material.class);
            material.id = document.getId();

            return material;
        }

        throw new NoMaterialFoundException("No material found with this id: " + id);
    }

    private Task<Void> updateTitleAsync(@NonNull String title) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("title", title);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updateTitle(@NonNull String title) {
        try {
            Task<Void> t = updateTitleAsync(title);
            Tasks.await(t);
            setTitle(title);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Task<Void> updateDescriptionAsync(@NonNull String description) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("description", description);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updateDescription(@NonNull String description) {
        try {
            Task<Void> t = updateDescriptionAsync(description);
            Tasks.await(t);
            setDescription(description);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Task<Void> updatePhotoAsync(@NonNull String photo) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("photo", photo);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updatePhoto(String photo) {
        try {
            Task<Void> t = updatePhotoAsync(photo);
            Tasks.await(t);
            setPhoto(photo);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateRenterAsync(DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (user == null)
                docRef.update("isRent", false);
            else
                docRef.update("isRent", true);
            return docRef.update("renter", user);
        }
        else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updateRenter(User user) {
        try {
            Task<Void> t;
            if (user == null){
                t = updateRenterAsync(null);
                setRenter(null);
                this.isRent = false;
            }
            else {
                DocumentReference docUser = getReference("users", user.getId());
                t = updateRenterAsync(docUser);
                setRenter(docUser);
                this.isRent = true;
            }
            Tasks.await(t);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateExpiryDateAsync(Timestamp date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("expiryDate", date);
        }else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    public boolean updateExpiryDate(Date date) {
        try {
            Timestamp timestamp = new Timestamp(date);
            Task<Void> t = this.updateExpiryDateAsync(timestamp);
            Tasks.await(t);
            setExpiryDate(timestamp);
            return true;
        } catch (ExecutionException | InterruptedException | NoLendingInProgressFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //funzione per mattia!
    //TODO controllare la data di scadenza
    public static ArrayList<Material> getRentableMaterials(double latitude, double longitude, double radiusInKm)
            throws ExecutionException, InterruptedException {
        ArrayList<Material> arr = new ArrayList<>();

        Query query = getInstance().collection(table).whereEqualTo("isRent", false);

        List<DocumentSnapshot> documents = getGeoQueries(query, radiusInKm * 1000,
                new GeoLocation(latitude, longitude));

        Timestamp timestamp = new Timestamp(new Date());

        for (DocumentSnapshot doc : documents) {
            Material material = Material.getMaterialById(doc.getId());
            if(material.expiryDate.compareTo(timestamp) > 0) {
                arr.add(material);
            }
        }

        return arr;
    }
}