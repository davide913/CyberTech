package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.*;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Geoquerable;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;

public class QuarantineAssistance extends Geoquerable {
    private final static String table = "quarantineAssistance";
    private DocumentReference assistanceType;
    private String description;
    private DocumentReference inCharge;
    private boolean isInCharge;
    private Timestamp deliveryDate;
    private GeoPoint location;
    private String geohash;
    private String title;
    private String id;
    //private raccolta chatPrivata

    public QuarantineAssistance() {}

    private QuarantineAssistance(DocumentReference assistanceType, String description, Timestamp deliveryDate,
                                GeoPoint location, String geohash, String title, String id) {
        this.assistanceType = assistanceType;
        this.description = description;
        this.isInCharge = false;
        this.deliveryDate = deliveryDate;
        this.location = location;
        this.geohash = geohash;
        this.title = title;
        this.id = id;
    }

    public AssistanceType getAssistanceTypeMaterialized() throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getDocument(assistanceType);

        if(document.exists())
            return AssistanceType.getAssistanceTypeById(document.getId());

        return null;
    }

    public DocumentReference getAssistanceType(){
        return assistanceType;
    }

    private void setAssistanceType(DocumentReference assistanceType) {
        this.assistanceType = assistanceType;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public DocumentReference getInCharge(){
        return inCharge;
    }

    public User getInChargeMaterialized() throws InterruptedException, ExecutionException {
        DocumentSnapshot document = getDocument(inCharge);

        if(document.exists())
            return User.getUserById(document.getId());

        return null;
    }

    private void setInCharge(DocumentReference inCharge) {
        this.inCharge = inCharge;
    }

    public String getGeohash() {
        return geohash;
    }

    private void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public Date getDateDeliveryToDate() {
        return deliveryDate.toDate();
    }

    private void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public GeoPoint getLocation() {
        return location;
    }

    private void setLocation(GeoPoint location) {
        this.location = location;
    }

    private boolean isInCharge() {
        return isInCharge;
    }

    private void setIsInCharge(boolean isInCharge) {
        this.isInCharge = isInCharge;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    //tested, modificata tolto lo user inCharge
    public static QuarantineAssistance createQuarantineAssistance(@NonNull AssistanceType assistanceType, String title,
                                                                  String description, Date date, double latitude, double longitude) throws ExecutionException, InterruptedException {
        DocumentReference AssTypeRef = getReference("assistanceType", assistanceType.getID());
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));

        Timestamp t = new Timestamp(date);

        Map<String, Object> myQuarantine = new HashMap<>();
        myQuarantine.put("description", description);
        myQuarantine.put("deliveryDate", t);
        myQuarantine.put("assistanceType", AssTypeRef);
        myQuarantine.put("location", geoPoint);
        myQuarantine.put("geohash", geohash);
        myQuarantine.put("title", title);
        myQuarantine.put("isInCharge", false);

        DocumentReference addedDocRef = Database.addToCollection(table, myQuarantine);

        return new QuarantineAssistance(AssTypeRef, description, t, geoPoint, geohash, title, addedDocRef.getId());
    }

    //tested
    public void removeQuarantineAssistance() throws ExecutionException, InterruptedException {
        deleteFromCollection(table, this.id);

        this.id = null;
    }

    //tested
    public static QuarantineAssistance getQuarantineAssistanceById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            QuarantineAssistance quarantineAssistance = document.toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(document.getId());

            return quarantineAssistance;
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);
    }

    //tested
    private Task<Void> updateAssistanceType_QuarantineAssistanceAsync(@NonNull AssistanceType assistanceType) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            DocumentReference docRefAssistance = getReference("assistanceType", assistanceType.getID());
            return docRef.update("assistanceType", docRefAssistance);

        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);
    }

    public boolean updateAssistanceType_QuarantineAssistance(@NonNull AssistanceType assistanceType) {
        try {
            Task<Void> t = this.updateAssistanceType_QuarantineAssistanceAsync(assistanceType);
            Tasks.await(t);
            this.setAssistanceType(getReference("assistanceType", assistanceType.getID()));
            return true;
        } catch (ExecutionException | InterruptedException | NoQuarantineAssistanceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //tested, aggiornata 6/12/2021 con modifica del campo isInCharge
    //TODO vedere se conviene tenere il controllo se l'utente é realemnte presente nel db
    private Task<Void> updateInCharge_QuarantineAssistanceAsync(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if(user == null) {
            docRef.update("isInCharge", false);
            return docRef.update("inCharge", FieldValue.delete());
        }

        DocumentReference userDoc = getReference("users", user.getId());
        DocumentSnapshot documentUser = getDocument(userDoc);

        if (document.exists() && documentUser.exists()) {
            docRef.update("isInCharge", true);
            return docRef.update("inCharge", userDoc);
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id + " Or no user found with this id: " + user.getId());
    }

    //tested, aggiornata 6/12/2021 con modifica del campo isInCharge
    public boolean updateInCharge_QuarantineAssistance(User user)  {
        try {
            Task<Void> t = this.updateInCharge_QuarantineAssistanceAsync(user);
            Tasks.await(t);
            if(user != null) {
                this.setInCharge(getReference("users", user.getId()));
                this.setIsInCharge(true);
            }
            else{
                this.setInCharge(null);
                this.setIsInCharge(false);
            }
            return true;
        } catch (ExecutionException | InterruptedException | NoQuarantineAssistanceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    //tested
    /*public boolean updateDeliveryDate(@NonNull Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("date", date);
            this.setDeliveryDate(new Timestamp(date));
            return true;
        } else
            return false;
    }*/

    public boolean updateDescription(@NonNull String description) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("description", description);
            this.setDescription(description);
            return true;
        } else
            return false;
    }

    //query che mi ritorna la richiesta dove incharge = user passato, fatta 7/12/2021
    public static QuarantineAssistance getQuarantineAssistanceByInCharge(User user) throws ExecutionException, InterruptedException {
        ArrayList<QuarantineAssistance> arr = new ArrayList<>();
        FirebaseFirestore db = getInstance();

        Task<QuerySnapshot> future = db.collection(table)
                .whereEqualTo("isInCharge", true)
                .whereEqualTo("inCharge", getReference("users", user.getId())).get();

        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if(documents.size() != 0) {
            QuarantineAssistance quarantineAssistance = documents.get(0).toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(documents.get(0).getId());

            return quarantineAssistance;
        }
        return null;
    }

    //modificata 30/11/2021, testata completa e aggiunta modifica per la gestione dei 6/12/2021,
    public static ArrayList<QuarantineAssistance> getJoinableQuarantineAssistance(AssistanceType type, GeoPoint position,
                                                                                  double radiusInKm) throws ExecutionException, InterruptedException {
        ArrayList<QuarantineAssistance> arr = new ArrayList<>();
        FirebaseFirestore db = getInstance();

        Query query = db.collection(table)
                .whereEqualTo("isInCharge", false);

        if(type != null)
            query = query.whereEqualTo("assistanceType", getReference("assistanceType", type.getID()));

        List<DocumentSnapshot> documents;

        if(position == null) {
            Task<QuerySnapshot> future = query.get();
            Tasks.await(future);
            documents = future.getResult().getDocuments();
        }
        else{
            documents = getGeoQueries(query, radiusInKm * 1000,
                    new GeoLocation(position.getLatitude(), position.getLongitude()));
        }

        for (DocumentSnapshot doc : documents) {
            QuarantineAssistance assistance = null;

            assistance = doc.toObject(QuarantineAssistance.class);
            assistance.id = doc.getId();
            arr.add(assistance);
        }

        arr.sort(new Comparator<QuarantineAssistance>() {
            @Override
            public int compare(QuarantineAssistance o1, QuarantineAssistance o2) {
                return o1.getDateDeliveryToDate().compareTo(o2.getDateDeliveryToDate());
            }
        });

        return arr;
    }
}
