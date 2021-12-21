package it.unive.cybertech.database.Profile;


import static it.unive.cybertech.database.Database.addToCollection;
import static it.unive.cybertech.database.Database.deleteFromCollection;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;


//TODO fare test delle funzioni scritte, NESSUNA É TESTATA
public class LendingInProgress {
    private final static String table = "lendingInProgress";
    private DocumentReference idMaterial;
    private Timestamp expiryDate;
    private Timestamp endExpiryDate;
    private String id;
    private Material material;

    public LendingInProgress() {}

    private LendingInProgress(String id, DocumentReference IdMaterial, Timestamp expiryDate) {
        this.id = id;
        this.idMaterial = IdMaterial;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
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

    public Timestamp getEndExpiryDate() {
        return endExpiryDate;
    }

    private void setEndExpiryDate(Timestamp endExpiryDate) {
        this.endExpiryDate = endExpiryDate;
    }

    public Material getMaterial() throws ExecutionException, InterruptedException {
        if(material == null)
            material = Material.getMaterialById(idMaterial.getId());
        return material;
    }

    public static LendingInProgress createLendingInProgress(Material material, Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRefMaterial = getReference("material", material.getId());

        Timestamp t = new Timestamp(date);

        Map<String, Object> mylending = new HashMap<>();          //create "table"
        mylending.put("date", t);
        mylending.put("material", docRefMaterial);

        DocumentReference addedDocRef = addToCollection(table, mylending);

        return new LendingInProgress(addedDocRef.getId(), docRefMaterial, t);
    }

    protected static LendingInProgress getLendingInProgressById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        LendingInProgress lending = null;

        if (document.exists()) {
            lending = document.toObject(LendingInProgress.class);
            lending.setId(document.getId());

            return lending;
        } else
            throw new NoLendingInProgressFoundException("No lending in progress found with this id: " + id);
    }

    public boolean removeLendingInProgress() {
        return deleteFromCollection(table, this.id);
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

    private Task<Void> updateEndExpiryDateAsync(Timestamp date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("endExpiryDate", date);
        }else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    public boolean updateEndExpiryDate(Date date) {
        try {
            Timestamp timestamp = new Timestamp(date);
            Task<Void> t = this.updateEndExpiryDateAsync(timestamp);
            Tasks.await(t);
            setEndExpiryDate(timestamp);
            return true;
        } catch (ExecutionException | InterruptedException | NoLendingInProgressFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Task<Void> updateMaterialAsync(Material material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (material == null)
                return docRef.update("material", FieldValue.delete());

            DocumentReference docRefMaterial = getReference("material", material.getId());
            return docRef.update("material", docRefMaterial);
        }
        else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }


    public boolean updateMaterial(Material material) {
        try {
            Task<Void> t = this.updateMaterialAsync(material);
            Tasks.await(t);
            if(material == null){
                this.idMaterial = null;
                this.material = null;
            }
            else {
                this.idMaterial = getReference("material", material.getId());
                this.material = material;
            }
            return true;
        } catch (ExecutionException | InterruptedException | NoLendingInProgressFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof LendingInProgress) {
            LendingInProgress lending = (LendingInProgress) o;
            return lending.getId().equals(this.getId());
        }
        return false;
    }
}
