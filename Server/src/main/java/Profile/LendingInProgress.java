package Profile;

import Material.Material;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static Connection.Connection.getDocument;
import static Connection.Connection.getReference;

//TODO fare test delle funzioni scritte, NESSUNA É TESTATA
public class LendingInProgress {

    private DocumentReference IDmaterial;
    private Timestamp ExpiryDate;
    private String Id;

    public LendingInProgress(){}

    private LendingInProgress(String id, DocumentReference IDmAterial, Timestamp expiryDate) {
        this.Id = id;
        this.IDmaterial = IDmAterial;
        ExpiryDate = expiryDate;
    }

    public DocumentReference getIDmaterial() {
        return IDmaterial;
    }

    private void setIDmaterial(DocumentReference IDmaterial) {
        this.IDmaterial = IDmaterial;
    }

    public Timestamp getExpiryDate() {
        return this.ExpiryDate;
    }

    public Date getDateExpiryDate() {
        return this.ExpiryDate.toDate();
    }

    private void setExpiryDate(Timestamp expiryDate) {
        ExpiryDate = expiryDate;
    }

    public static LendingInProgress addLendingInProgress(Material material, Date date) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        DocumentReference docRefMaterial = db.collection("material").document(material.getId());

        Timestamp t = Timestamp.of(date);

        Map<String, Object> mylending = new HashMap<>();          //create "table"
        mylending.put("date", t);
        mylending.put("material", docRefMaterial);

        ApiFuture<DocumentReference> addedDocRef = db.collection("lendingInProgress").add(mylending);

        return new LendingInProgress(addedDocRef.get().getId(), docRefMaterial, t );
    }

    public void removeLendingInProgress(){
        Firestore db = FirestoreClient.getFirestore();      //create of object db
        db.collection("lendingInProgress").document(this.Id).delete();
    }

    public boolean updateExpiryDate(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", this.Id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists()) {
            Timestamp t = Timestamp.of(date);
            docRef.update("date", t);
            this.ExpiryDate = t;

            return true;
        }
        else
            return false;
    }

    public boolean updateMaterial(Material material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", this.Id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists()) {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRefMaterial = db.collection("material").document(material.getId());

            docRef.update("date", docRefMaterial);
            this.IDmaterial = docRefMaterial;

            return true;
        }
        else
            return false;
    }

    /*public boolean updateExpiryDate(Date date){
        DocumentReference docRef = getReference("lendingInProgress", this.Id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists()) {
            DocumentReference docRefAssistance = user.getDocumentReference();
            docRef.update("users", docRefAssistance);
            this.InCharge = docRefAssistance;

            return true;
        }
        else
            return false;
    }*/

    @Override
    public boolean equals(Object o){
        if(o instanceof LendingInProgress){
            LendingInProgress lending = (LendingInProgress) o;
            return lending.ExpiryDate.equals(this.ExpiryDate)  && lending.IDmaterial==this.IDmaterial;
        }
        return false;
    }
}
