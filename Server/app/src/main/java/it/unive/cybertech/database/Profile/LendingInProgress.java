package it.unive.cybertech.database.Profile;


import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.deleteFromCollection;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;


//TODO fare test delle funzioni scritte, NESSUNA É TESTATA
public class LendingInProgress {

    private DocumentReference idMaterial;
    private Timestamp expiryDate;
    private String id;

    public LendingInProgress() {}

    private LendingInProgress(String id, DocumentReference IDmAterial, Timestamp expiryDate) {
        this.id = id;
        this.idMaterial = IDmAterial;
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

    public static LendingInProgress createLendingInProgress(Material material, Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRefMaterial = getReference("material", material.getId());

        Timestamp t = new Timestamp(date);

        Map<String, Object> mylending = new HashMap<>();          //create "table"
        mylending.put("date", t);
        mylending.put("material", docRefMaterial);

        DocumentReference addedDocRef = addToCollection("lendingInProgress", mylending);

        return new LendingInProgress(addedDocRef.getId(), docRefMaterial, t);
    }

    protected static LendingInProgress getLendingInProgressById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", id);
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
        return deleteFromCollection("lendingInProgress", this.id);
    }

    public Task<Void> updateExpiryDateAsync(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            Timestamp t = new Timestamp(date);
            return docRef.update("date", t);
        }else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    public boolean updateExpiryDate(Date date) {
        try {
            Task<Void> t = this.updateExpiryDateAsync(date);
            Tasks.await(t);
            this.expiryDate = new Timestamp(date);
            return true;
        } catch (ExecutionException | InterruptedException | NoLendingInProgressFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Task<Void> updateMaterialAsync(DocumentReference material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("material", material);
        else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }


    public boolean updateMaterial(@NonNull Material material) {
        try {
            DocumentReference docRefMaterial = getReference("material", material.getId());
            Task<Void> t = this.updateMaterialAsync(docRefMaterial);
            Tasks.await(t);
            this.idMaterial = docRefMaterial;
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
            return lending.expiryDate.equals(this.expiryDate) && lending.idMaterial == this.idMaterial;
        }
        return false;
    }
}
