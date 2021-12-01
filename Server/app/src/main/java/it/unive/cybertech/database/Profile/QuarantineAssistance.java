package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.*;

import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;

//ALL TESTED
public class QuarantineAssistance {
    private DocumentReference assistanceType;
    private String description;
    private DocumentReference inCharge;
    private Timestamp deliveryDate;
    private GeoPoint location;
    private String title;
    private String id;
    //private raccolta chatPrivata

    //TODO funzione che ritorna tutte le richieste con inCharge == null


    public QuarantineAssistance() {}

    private QuarantineAssistance(DocumentReference assistanceType, String description,
                                DocumentReference inCharge, Timestamp deliveryDate,
                                GeoPoint location, String title, String id) {
        this.assistanceType = assistanceType;
        this.description = description;
        this.inCharge = inCharge;
        this.deliveryDate = deliveryDate;
        this.location = location;
        this.title = title;
        this.id = id;
    }

    public AssistanceType getAssistanceType() throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getDocument(assistanceType);

        if(document.exists())
            return AssistanceType.getAssistanceTypeById(document.getId());

        return null;
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

    public User getInCharge() throws InterruptedException, ExecutionException {
        DocumentSnapshot document = getDocument(inCharge);

        if(document.exists())
            return User.getUserById(document.getId());

        return null;
    }

    private void setInCharge(DocumentReference inCharge) {
        this.inCharge = inCharge;
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

    public Date getDateDeliveryDate() {
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

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    //tested
    public static QuarantineAssistance createQuarantineAssistance(AssistanceType assistanceType, String title, String description,
                                                                  User user, Date date, int latitude, int longitude) throws ExecutionException, InterruptedException {
        DocumentReference AssTypeRef = getReference("assistanceType", assistanceType.getID());
        DocumentReference userRef = getReference("users", user.getId());
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);

        Timestamp t = new Timestamp(date);

        Map<String, Object> myQuarantine = new HashMap<>();
        myQuarantine.put("description", description);
        myQuarantine.put("inCharge", userRef);
        myQuarantine.put("deliveryDate", t);
        myQuarantine.put("assistanceType", AssTypeRef);
        myQuarantine.put("location", geoPoint);
        myQuarantine.put("title", title);

        DocumentReference addedDocRef = Database.addToCollection("quarantineAssistance", myQuarantine);

        return new QuarantineAssistance(AssTypeRef, description, userRef, t, geoPoint, title, addedDocRef.getId());
    }

    //tested
    public void removeQuarantineAssistance() throws ExecutionException, InterruptedException {
        deleteFromCollection("quarantineAssistance", this.id);

        this.assistanceType = null;
        this.inCharge = null;
        this.id = null;
        this.deliveryDate = null;
        this.description = null;
    }

    //tested
    protected static QuarantineAssistance getQuarantineAssistanceById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            QuarantineAssistance quarantineAssistance = document.toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(document.getId());

            return quarantineAssistance;
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);
    }



    //modificata 30/11/2021, testata manca la "storia" del geopoint e il where equal to a documentReference non funziona ( fatto a mano )
    public static ArrayList<QuarantineAssistance> getJoinableQuarantineAssistance(AssistanceType type, GeoPoint location,
                                                                                  double radius, Date date) throws ExecutionException, InterruptedException {
        ArrayList<QuarantineAssistance> arr = new ArrayList<>();
        FirebaseFirestore db = getInstance();

        Query query = db.collection("quarantineAssistance");

        if(type != null){
            DocumentReference document = getReference("assistanceType", type.getID());
            Log.d("log", document.getPath());
            query = query.whereEqualTo("assistanceType", document);
        }

        if(date != null){
            Timestamp timestamp = new Timestamp(date);
            query = query.whereGreaterThanOrEqualTo("date", timestamp);
        }

        //TODO fare il filtro per la geopoint

        Task<QuerySnapshot> future = query.get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            QuarantineAssistance assistance = null;

            assistance = doc.toObject(QuarantineAssistance.class);

            if (assistance.inCharge == null ) {
                assistance.id = doc.getId();
                arr.add(assistance);
            }
        }

        return arr;
    }

    //tested
    private Task<Void> updateAssistanceType_QuarantineAssistanceAsync(@NonNull AssistanceType assistanceType) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
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
            this.assistanceType = getReference("assistanceType", assistanceType.getID());
            return true;
        } catch (ExecutionException | InterruptedException | NoQuarantineAssistanceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //tested
    //TODO vedere se conviene tenere il controllo se l'utente é realemnte presente nel db
    private Task<Void> updateInCharge_QuarantineAssistanceAsync(User user) throws Exception {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if(user == null)
            return docRef.update("inCharge", FieldValue.delete());

        DocumentReference userDoc = getReference("users", user.getId());
        DocumentSnapshot documentUser = getDocument(userDoc);

        if (document.exists() && documentUser.exists()) {
            return docRef.update("inCharge", userDoc);
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id + " Or no user found with this id: " + user.getId());
    }

    //tested
    public boolean updateInCharge_QuarantineAssistance(User user)  {
        try {
            Task<Void> t = this.updateInCharge_QuarantineAssistanceAsync(user);
            Tasks.await(t);
            this.inCharge = getReference("users", user.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //tested
    public boolean updateDeliveryDate(@NonNull Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("date", date);
            this.deliveryDate = new Timestamp(date);

            return true;
        } else
            return false;
    }

    //TODO finire la parte di QuarantineAssistance
}
