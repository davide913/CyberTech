package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.*;
import static it.unive.cybertech.database.Profile.AssistanceType.*;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;

public class QuarantineAssistance {
    private DocumentReference assistanceType;
    private String description;
    private DocumentReference inCharge;
    private Timestamp deliveryDate;
    private String id;
    //private raccolta chatPrivata


    public QuarantineAssistance() {
        description = new String("Quarantine Assistance empty");
    }

    private QuarantineAssistance(DocumentReference assistanceType, String description, DocumentReference inCharge,
                                 Timestamp deliveryDate, String id) {
        this.assistanceType = assistanceType;
        this.description = description;
        this.inCharge = inCharge;
        this.deliveryDate = deliveryDate;
        this.id = id;
    }


    public AssistanceType getAssistanceType() {
        if (this.assistanceType == null)
            return null;
        Task<DocumentSnapshot> val = assistanceType.get();
        DocumentSnapshot document = val.getResult();

        AssistanceType assistanceType = document.toObject(AssistanceType.class);

        return assistanceType;//.getAssistanceType(assistanceType.getType());
    }

    private void setAssistanceType(DocumentReference assistanceType) {
        this.assistanceType = assistanceType;
    }

    //non farei la set ma piuttosto gestirei tutto con un update
    /*public void setAssistanceType(AssistanceType assistanceType) {
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        AssistanceType = db.collection("assistanceType").document(assistanceType.getType());
    }*/

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public User getInCharge() throws Exception {
        Task<DocumentSnapshot> val = inCharge.get();
        DocumentSnapshot document = val.getResult();

        return User.getUserById(document.getId());
    }

    private void setInCharge(DocumentReference inCharge) {
        this.inCharge = inCharge;
    }

    //gestita dall'update
    /*private void setInChargeUser(User inCharge) {
        InCharge = inCharge.getDocumentReference();
    }*/

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

    //private function for code replication
    private boolean unusedAssistanceType() {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("quarantineAssistance").
                whereEqualTo("assistanceType", this.assistanceType).get();
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        return documents.isEmpty();
    }


    //tested
    public static QuarantineAssistance addQuarantineAssistance(String typeAssistance, String description, User user, Date date) throws ExecutionException, InterruptedException {

        try {       //try to add the assistance type, if exist this branch just get that object
            addAssistanceType(typeAssistance);
        } catch (NoAssistanceTypeFoundException e) {
        }
        //TODO getAssistanceType??? a cosa serve? non è statico, va in errore......
        AssistanceType assistanceType = null;//getAssistanceType(typeAssistance);

        DocumentReference AssTypeRef = getReference("assistanceType", assistanceType.getID());//db.collection("assistanceType").document(assistanceType.getID());
        DocumentReference userRef = getReference("users", user.getId());

        Timestamp t = new Timestamp(date);

        Map<String, Object> myQuarantine = new HashMap<>();          //create "table"
        myQuarantine.put("description", description);
        myQuarantine.put("user", userRef);
        myQuarantine.put("date", t);
        myQuarantine.put("assistanceType", AssTypeRef);

        DocumentReference addedDocRef = Database.addToCollection("quarantineAssistance", myQuarantine);//db.collection("quarantineAssistance").add(myQuarantine);

        return new QuarantineAssistance(AssTypeRef, description, userRef, t, addedDocRef.getId());
    }

    //tested
    public void removeQuarantineAssistance() throws ExecutionException, InterruptedException {
        deleteFromCollection("quarantineAssistance", this.id);//db.collection("quarantineAssistance").document(this.id).delete();

        //if there isn't any quarantine assistance that refer to the Assistance type is possible to remove it
        if (this.unusedAssistanceType())
            removeAssistanceType(this.getAssistanceType().getType());
    }

    //tested
    public static QuarantineAssistance getQuarantineAssistance(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        QuarantineAssistance quarantineAssistance = null;

        if (document.exists()) {
            quarantineAssistance = document.toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(document.getId());

            return quarantineAssistance;
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);

    }

    //tested
    public boolean updateInCharge_QuarantineAssistance(@NonNull AssistanceType assistanceType) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            DocumentReference docRefAssistance = assistanceType.getDocumentReference();
            docRef.update("assistanceType", docRefAssistance);

            if (this.unusedAssistanceType())
                removeAssistanceType(this.getAssistanceType().getType());

            this.assistanceType = docRefAssistance;

            return true;
        } else
            return false;
    }

    //tested
    public boolean updateAssistanceType_QuarantineAssistance(@NonNull User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            DocumentReference docRefAssistance = user.getDocumentReference();
            docRef.update("users", docRefAssistance);
            this.inCharge = docRefAssistance;

            return true;
        } else
            return false;
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

    public static void main(String[] args) throws Exception {
        //Connection.initializeConnection();

        //User u = User.getUserById("S2BaLtNi3Zja76BMWGXH");
        //User s = User.createUser("davide", "finesso", "M", "rosmini", "abano", "italy",
        //                  new GeoPoint(1.4,1.5),true );


        QuarantineAssistance q = getQuarantineAssistance("TnLz3hORQl9RpqaHsNK4");

        q.updateDeliveryDate(new Date(2021, 9, 10));
        //System.out.println( q.updateAssistanceType_QuarantineAssistance(s) );
        //q.updateAssistanceType_QuarantineAssistance(get;


        //q.removeQuarantineAssistance();
    }
}
