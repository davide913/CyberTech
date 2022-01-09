package it.unive.cybertech.database.Material;

import static it.unive.cybertech.database.Database.addToCollection;
import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Geoquerable;
import it.unive.cybertech.database.Material.Exception.NoMaterialFoundException;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.database.Profile.User;

/**
 * Class use to describe a user's material instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 *
 * @author Davide Finesso
 */
public class Material extends Geoquerable {
    public final static String table = "material";
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

    /**
     * Materialize field for increase the performance.
     *
     * @author Davide Finesso
     */
    private User materializeOwner;
    private User materializeRenter;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public Material() {}

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
    private Material(String id, DocumentReference ownerReference, String title, String description, String photo,
                     DocumentReference renter, boolean isRent, GeoPoint location, String geohash,
                     DocumentReference type, Timestamp expiryDate, User owner) {
        this.id = id;
        this.owner = ownerReference;
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.renter = renter;
        this.isRent = isRent;
        this.location = location;
        this.geohash = geohash;
        this.type = type;
        this.expiryDate = expiryDate;
        this.materializeOwner = owner;
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

    private boolean isRent() {
        return isRent;
    }

    private void setRent(boolean rent) {
        isRent = rent;
    }

    /**
     * The method return the field renter materialize, if the material has no renter it throw an exception.
     *
     * @author Davide Finesso
     */
    public User obtainMaterializedRenter() throws ExecutionException, InterruptedException, NoMaterialFoundException {
        if(renter == null) {
            if (materializeRenter != null)
                materializeRenter = User.obtainUserById(renter.getId());

            return materializeRenter;
        }
        else throw new NoMaterialFoundException("The material ("+ id +") has no renter");
    }

    /**
     * The method return the field owner materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public User obtainMaterializedOwner() throws ExecutionException, InterruptedException {
        if(materializeOwner == null)
            materializeOwner = User.obtainUserById(owner.getId());

        return materializeOwner;
    }

    /**
     * The method add to the database a new material and return it.
     *
     * @author Davide Finesso
     */
    public static Material createMaterial(@NonNull User owner, String title, String description, String photo,
                                          @NonNull Type type, double latitude, double longitude, Date date)
            throws ExecutionException, InterruptedException {

        DocumentReference docPro = getReference(User.table, owner.getId());
        DocumentReference docType = getReference(Type.table, type.getId());
        String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
        Timestamp timestamp = new Timestamp(date);
        GeoPoint location = new GeoPoint(latitude, longitude);

        Map<String, Object> myMaterial = new HashMap<>();
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
                photo, null, false, location, geohash, docType, timestamp, owner);
    }

    /**
     * The method return the material with that id. If there isn't a material with that id it throw an exception.
     *
     * @author Davide Finesso
     */
    public static Material obtainMaterialById(@NonNull String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Material material = null;

        if (document.exists()) {
            material = document.toObject(Material.class);
            material.id = document.getId();

            return material;
        }

        throw new NoMaterialFoundException("No material found with this id: " + id);
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> deleteMaterialAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
        else
            throw new NoMaterialFoundException("No material found with this id: " + id);
    }

    /**
     * The method is use to delete a material from the database.
     * Before delete the method check if there is any lending in progress associate with this material and delete it.
     * It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean deleteMaterial() {
        try {
            try {
                this.obtainLending().deleteLendingInProgress();
            }
            catch ( NoLendingInProgressFoundException exception ){};

            Task<Void> t = deleteMaterialAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateTitleAsync(@NonNull String title) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("title", title);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    /**
     * The method is use to update a material field title to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateDescriptionAsync(@NonNull String description) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("description", description);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    /**
     * The method is use to update a material field description to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updatePhotoAsync(@NonNull String photo) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("photo", photo);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    /**
     * The method is use to update a material field photo to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateRenterAsync(DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (user == null)
                docRef.update("isRent", false);
            else
                docRef.update("isRent", true);
            return docRef.update("renter", user);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    /**
     * The method is use to update a material field renter to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateRenter(User user) {
        try {
            Task<Void> t;
            if (user == null) {
                t = updateRenterAsync(null);
                setRenter(null);
                this.isRent = false;
            } else {
                DocumentReference docUser = getReference(User.table, user.getId());
                t = updateRenterAsync(docUser);
                setRenter(docUser);
                this.isRent = true;
            }
            this.materializeRenter = user;
            Tasks.await(t);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateExpiryDateAsync(Timestamp date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("expiryDate", date);
        } else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    /**
     * The method is use to update a material field expiry date to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * The method is use to get the lending in progress associate to this material and return it.
     * if there isn't any landing associate it throw an exception
     *
     * @author Davide Finesso
     */
    public LendingInProgress obtainLending() throws ExecutionException, InterruptedException {

        Task<QuerySnapshot> task = getInstance().collection(LendingInProgress.table)
                .whereEqualTo("material", getReference(table, id)).get();
        Tasks.await(task);
        List<DocumentSnapshot> list = task.getResult().getDocuments();
        if (!list.isEmpty())
            return LendingInProgress.obtainLendingInProgressById(list.get(0).getId());

        throw new NoLendingInProgressFoundException("No lending found connect to this material: " + id);
    }

    /**
     * The method is use to get all the material that can be rent in a specify area.
     *
     * @author Davide Finesso
     */
    public static List<Material> obtainRentableMaterials(double latitude, double longitude, double radiusInKm, String userId)
            throws ExecutionException, InterruptedException {
        ArrayList<Material> arr = new ArrayList<>();

        Query query = getInstance().collection(table)
                .whereEqualTo("isRent", false);

        List<DocumentSnapshot> documents = getGeoQueries(query, radiusInKm * 1000,
                new GeoLocation(latitude, longitude));

        Timestamp timestamp = new Timestamp(new Date());
        DocumentReference userRef = getReference(User.table, userId);
        for (DocumentSnapshot doc : documents) {
            Timestamp t = doc.getTimestamp("expiryDate");
            if (t != null && t.compareTo(timestamp) > 0 && doc.getDocumentReference("owner") != userRef) {
                arr.add(Material.obtainMaterialById(doc.getId()));
            }
        }

        return arr;
    }

    /**
     * Compare their id because are unique.
     *
     * @author Davide Finesso
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equals(id, material.id);
    }

    /**
     * Return the hash by the unique field id.
     *
     * @author Davide Finesso
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}