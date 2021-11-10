package Profile;

import Connection.Connection;
import Profile.Exception.NoAssistanceTypeFoundExeption;
import Profile.Exception.NoQuarantineAssistanceFoundException;
import Profile.Exception.NoUserFoundExeption;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.internal.NonNull;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QuarantineAssistance {
    private DocumentReference AssistanceType;
    private String Description;
    private DocumentReference InCharge;
    private Timestamp DeliveryDate;
    private String Id;
    //private raccolta chatPrivata


    public QuarantineAssistance() {
        Description = new String("Quarantine Assistance empty");
    }

    private QuarantineAssistance(DocumentReference assistanceType, String description, DocumentReference inCharge,
                                 Timestamp deliveryDate, String id) {
        AssistanceType = assistanceType;
        Description = description;
        InCharge = inCharge;
        DeliveryDate = deliveryDate;
        Id = id;
    }



    public AssistanceType getAssistanceType() throws ExecutionException, InterruptedException {
        if( this.AssistanceType == null)
            return null;
        ApiFuture<DocumentSnapshot> val = AssistanceType.get();
        DocumentSnapshot document = val.get();

        Profile.AssistanceType assistanceType = document.toObject(Profile.AssistanceType.class);

        return Profile.AssistanceType.getAssistanceType(assistanceType.getType());
    }

    private void setAssistanceType(DocumentReference assistanceType) {
        AssistanceType = assistanceType;
    }

    //non farei la set ma piuttosto gestirei tutto con un update
    /*public void setAssistanceType(AssistanceType assistanceType) {
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        AssistanceType = db.collection("assistanceType").document(assistanceType.getType());
    }*/

    public String getDescription() {
        return Description;
    }

    private void setDescription(String description) {
        Description = description;
    }

    public Users getInCharge() throws Exception {
        ApiFuture<DocumentSnapshot> val = InCharge.get();
        DocumentSnapshot document = val.get();

        return Users.getUserById(document.getId());
    }

    private void setInCharge(DocumentReference inCharge) {
        InCharge = inCharge;
    }

    private void setInChargeUser(Users inCharge) {
        Firestore db = FirestoreClient.getFirestore();

        InCharge = db.collection("users").document(inCharge.getId());
    }

    public String getId() {
        return Id;
    }

    private void setId(String id) {
        Id = id;
    }

    public Timestamp getDeliveryDate() {
        return DeliveryDate;
    }
    public Date getDateDeliveryDate() {
        return DeliveryDate.toDate();
    }

    private void setDeliveryDate(Timestamp deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    //tested
    public static QuarantineAssistance addQuarantineAssistance(String typeAssistance, String description, Users user, Date date) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        try {       //try to add the assistance type, if exist this branch just get that object
            Profile.AssistanceType.addAssistanceType(typeAssistance);
        }
        catch(NoAssistanceTypeFoundExeption e){}

        Profile.AssistanceType assistanceType = Profile.AssistanceType.getAssistanceType(typeAssistance);

        DocumentReference AssTypeRef = db.collection("assistanceType").document(assistanceType.getID());
        DocumentReference userRef = getReference(user.getId());

        Timestamp t = Timestamp.of(date);

        Map<String, Object> myQuarantine = new HashMap<>();          //create "table"
        myQuarantine.put("description", description);
        myQuarantine.put("user", userRef);
        myQuarantine.put("date", t);
        myQuarantine.put("assistanceType", AssTypeRef);

        ApiFuture<DocumentReference> addedDocRef = db.collection("quarantineAssistance").add(myQuarantine);

        return new QuarantineAssistance(AssTypeRef, description, userRef, t, addedDocRef.get().getId());
    }

    //private function for code replication
    private boolean unusedAssistanceType() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        ApiFuture<QuerySnapshot> future = db.collection("quarantineAssistance").
                whereEqualTo("assistanceType", this.AssistanceType ).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.isEmpty();
    }

    //private function for code replication
    protected static DocumentReference getReference(String id){
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        return db.collection("quarantineAssistance").document(id);
    }

    //private function for code replication
    protected static DocumentSnapshot getDocument(DocumentReference reference) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> val = reference.get();
        return val.get();
    }

    //tested
    public void removeQuarantineAssistance() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db
        db.collection("quarantineAssistance").document(this.Id).delete();

        //if there isn't any quarantine assistance that refer to the Assistance type is possible to remove it
        if(this.unusedAssistanceType())
            Profile.AssistanceType.removeAssistanceType(this.getAssistanceType().getType());
    }

    //tested
    public static QuarantineAssistance getQuarantineAssistance(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(id);
        DocumentSnapshot document = getDocument(docRef);

        QuarantineAssistance quarantineAssistance = null;

        if (document.exists()) {
            quarantineAssistance = document.toObject(QuarantineAssistance.class);
            quarantineAssistance.setId(document.getId());

            return quarantineAssistance;
        }
        else
            throw new NoQuarantineAssistanceFoundException("No quarantine assistance found with this id: "+ id);

    }

    //tested
    public boolean updateAssistanceType_QuarantineAssistance(@NonNull AssistanceType assistanceType) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(this.Id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists()) {
            DocumentReference docRefAssistance = assistanceType.getDocumentReference();
            docRef.update("assistanceType", docRefAssistance);

            if(this.unusedAssistanceType())
                Profile.AssistanceType.removeAssistanceType(this.getAssistanceType().getType());

            this.AssistanceType = docRefAssistance;

            return true;
        }
        else
            return false;
    }

    //TODO finire la parte di QuarantineAssistance

    public static void main(String[] args) throws Exception {
        Connection.initializeConnection();

        //Users u = Users.getUserById("S2BaLtNi3Zja76BMWGXH");

        QuarantineAssistance q = getQuarantineAssistance("TnLz3hORQl9RpqaHsNK4");

        //q.updateAssistanceType_QuarantineAssistance(get;


        //q.removeQuarantineAssistance();
    }
}
