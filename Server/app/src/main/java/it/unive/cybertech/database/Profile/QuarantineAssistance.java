package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.*;
import static it.unive.cybertech.database.Profile.AssistanceType.*;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;

public class QuarantineAssistance {
    private DocumentReference assistanceType;
    private String description;
    private DocumentReference inCharge;
    private Timestamp deliveryDate;
    private String id;
    //private raccolta chatPrivata


    public QuarantineAssistance() {}

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


    private boolean unusedAssistanceType() {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("quarantineAssistance").
                whereEqualTo("assistanceType", this.assistanceType).get();
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        return documents.isEmpty();
    }

    //tested
    public static QuarantineAssistance createQuarantineAssistance(String typeAssistance, String description, User user, Date date) throws ExecutionException, InterruptedException {
        AssistanceType assistanceType;

        try {       //try to add the assistance type, if exist this branch just get that object
            assistanceType = AssistanceType.createAssistanceType(typeAssistance);
        } catch (NoAssistanceTypeFoundException e) {
            assistanceType = AssistanceType.getAssistanceType(typeAssistance);
        }

        DocumentReference AssTypeRef = getReference("assistanceType", assistanceType.getID());//db.collection("assistanceType").document(assistanceType.getID());
        DocumentReference userRef = getReference("users", user.getId());

        Timestamp t = new Timestamp(date);

        Map<String, Object> myQuarantine = new HashMap<>();
        myQuarantine.put("description", description);
        myQuarantine.put("user", userRef);
        myQuarantine.put("date", t);
        myQuarantine.put("assistanceType", AssTypeRef);

        DocumentReference addedDocRef = Database.addToCollection("quarantineAssistance", myQuarantine);//db.collection("quarantineAssistance").add(myQuarantine);

        return new QuarantineAssistance(AssTypeRef, description, userRef, t, addedDocRef.getId());
    }

    //tested
    //TODO modificare i campi dell'oggetto elimiato dal db
    public void removeQuarantineAssistance() throws ExecutionException, InterruptedException {
        deleteFromCollection("quarantineAssistance", this.id);//db.collection("quarantineAssistance").document(this.id).delete();

        //if there isn't any quarantine assistance that refer to the Assistance type is possible to remove it
        /*if (this.unusedAssistanceType())
            this.getAssistanceType().deleteAssistanceType();*/
    }

    //tested
    public static QuarantineAssistance getQuarantineAssistance(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            QuarantineAssistance quarantineAssistance = document.toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(document.getId());

            return quarantineAssistance;
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id);

    }

    //tested;
    //TODO da vedere se conviene verificare se bisogna aggiungere l'assistanceType
    public Task<Void> updateAssistanceType_QuarantineAssistanceAsync(@NonNull AssistanceType assistanceType) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            DocumentReference docRefAssistance = getReference("assistanceType", assistanceType.getID());
            return docRef.update("assistanceType", docRefAssistance);

            /*if (this.unusedAssistanceType())
                this.getAssistanceType().deleteAssistanceType();*/
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
    public Task<Void> updateInCharge_QuarantineAssistanceAsync(@NonNull User user) throws Exception {
        /*DocumentReference userDoc;
        try{
            //userDoc = User.getUserById(user.getId()).getDocumentReference();
            userDoc = getReference("user", User.getUserById(user.getId()).getId());
        } catch (NoUserFoundException e) {
            throw new NoUserFoundException("No quarantine user found with this id: " + id);
        }*/

        DocumentReference userDoc = getReference("user", user.getId());
        DocumentSnapshot documentUser = getDocument(userDoc);

        DocumentReference docRef = getReference("quarantineAssistance", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists() && documentUser.exists()) {
            return docRef.update("user", userDoc);
        } else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: " + id + " Or no user found with this id: " + user.getId());
    }


    public boolean updateInCharge_QuarantineAssistance(@NonNull User user)  {
        try {
            Task<Void> t = this.updateInCharge_QuarantineAssistanceAsync(user);
            Tasks.await(t);
            this.inCharge = getReference("user", user.getId());
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
