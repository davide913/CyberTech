package it.unive.cybertech.database.Profile;


import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.deleteFromCollection;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Material.Material;


//TODO fare test delle funzioni scritte, NESSUNA É TESTATA
public class LendingInProgress {

    private DocumentReference idMaterial;
    private Timestamp expiryDate;
    private String id;

    public LendingInProgress() {
    }

    private LendingInProgress(String id, DocumentReference IDmAterial, Timestamp expiryDate) {
        this.id = id;
        this.idMaterial = IDmAterial;
        this.expiryDate = expiryDate;
    }

    public DocumentReference getIdMaterial() {
        return idMaterial;
    }

    private void setIdMaterial(DocumentReference idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Timestamp getExpiryDate() {
        return this.expiryDate;
    }

    public Date getDateExpiryDate() {
        return this.expiryDate.toDate();
    }

    private void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public static LendingInProgress addLendingInProgress(Material material, Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRefMaterial = getReference("", material.getId());//db.collection("material").document(material.getId());

        Timestamp t = new Timestamp(date);

        Map<String, Object> mylending = new HashMap<>();          //create "table"
        mylending.put("date", t);
        mylending.put("material", docRefMaterial);

        DocumentReference addedDocRef = addToCollection("lendingInProgress", mylending);

        return new LendingInProgress(addedDocRef.getId(), docRefMaterial, t);
    }

    public void removeLendingInProgress() {
        deleteFromCollection("lendingInProgress", this.id);//db.collection("lendingInProgress").document(this.id).delete();
    }

    public boolean updateExpiryDate(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            Timestamp t = new Timestamp(date);
            docRef.update("date", t);
            this.expiryDate = t;

            return true;
        } else
            return false;
    }

    public boolean updateMaterial(Material material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            DocumentReference docRefMaterial = getReference("material",material.getId());//db.collection("material").document(material.getId());

            docRef.update("date", docRefMaterial);
            this.idMaterial = docRefMaterial;

            return true;
        } else
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
    public boolean equals(Object o) {
        if (o instanceof LendingInProgress) {
            LendingInProgress lending = (LendingInProgress) o;
            return lending.expiryDate.equals(this.expiryDate) && lending.idMaterial == this.idMaterial;
        }
        return false;
    }
}
