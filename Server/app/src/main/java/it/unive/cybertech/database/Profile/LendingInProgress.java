package it.unive.cybertech.database.Profile;


import static it.unive.cybertech.database.Database.addToCollection;
import static it.unive.cybertech.database.Database.deleteFromCollection;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.LendingInProgressException;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;


//TODO fare test delle funzioni scritte, NESSUNA É TESTATA
public class LendingInProgress {
    public final static String table = "lendingInProgress";
    private DocumentReference material;
    private Timestamp expiryDate;
    private Timestamp endExpiryDate;
    private boolean waitingForFeedback;
    private String id;

    private Material materializeMaterial;

    public LendingInProgress() {
    }

    private LendingInProgress(String id, DocumentReference material, Timestamp expiryDate) {
        this.id = id;
        this.material = material;
        this.expiryDate = expiryDate;
        this.waitingForFeedback = false;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public DocumentReference getMaterial() {
        return material;
    }

    private void setMaterial(DocumentReference material) {
        this.material = material;
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

    public boolean getWaitingForFeedback() {
        return waitingForFeedback;
    }

    private void setWaitingForFeedback(boolean waitingForFeedback) {
        this.waitingForFeedback = waitingForFeedback;
    }

    public Material getMaterializedMaterial() throws ExecutionException, InterruptedException {
        if (materializeMaterial == null)
            materializeMaterial = Material.getMaterialById(material.getId());
        return materializeMaterial;
    }

    //03/01/2022 Aggiunto controllo se esiste già un lending con quel materiale. Solo un prestito alla volta è concesso
    public static LendingInProgress createLendingInProgress(Material material, Date expiryDate) throws ExecutionException, InterruptedException, LendingInProgressException {
        DocumentReference docRefMaterial = getReference(Material.table, material.getId());

        Task<QuerySnapshot> lending = getInstance().collection(LendingInProgress.table).whereEqualTo("material", docRefMaterial).get();
        Tasks.await(lending);
        if (lending.getResult().size() == 0) {
            Timestamp t = new Timestamp(expiryDate);

            Map<String, Object> mylending = new HashMap<>();          //create "table"
            mylending.put("expiryDate", t);
            mylending.put("waitingForFeedback", false);
            mylending.put("material", docRefMaterial);

            DocumentReference addedDocRef = addToCollection(table, mylending);

            return new LendingInProgress(addedDocRef.getId(), docRefMaterial, t);
        } else
            throw new LendingInProgressException("A lendign with this material is already in progress: " + material.getId());
    }

    public static LendingInProgress getLendingInProgressById(String id) throws ExecutionException, InterruptedException {
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
        } else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    public boolean updateExpiryDate(@NonNull Date date) {
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

    private Task<Void> updateWaitingForFeedbackAsync(boolean val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("waitingForFeedback", val);
        } else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    public boolean updateWaitingForFeedback(boolean val) {
        try {
            Task<Void> t = this.updateWaitingForFeedbackAsync(val);
            Tasks.await(t);
            setWaitingForFeedback(val);
            return true;
        } catch (ExecutionException | InterruptedException | NoLendingInProgressFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateEndExpiryDateAsync(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (date == null) {
                return docRef.update("endExpiryDate", FieldValue.delete());
            }
            return docRef.update("endExpiryDate", new Timestamp(date));
        } else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    public boolean updateEndExpiryDate(Date date) {
        try {
            Task<Void> t = this.updateEndExpiryDateAsync(date);
            Tasks.await(t);
            if (date == null)
                setEndExpiryDate(null);
            else
                setEndExpiryDate(new Timestamp(date));
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
                return docRef.update(Material.table, FieldValue.delete());

            DocumentReference docRefMaterial = getReference(Material.table, material.getId());
            return docRef.update("material", docRefMaterial);
        } else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }


    public boolean updateMaterial(Material material) {
        try {
            Task<Void> t = this.updateMaterialAsync(material);
            Tasks.await(t);
            if (material == null) {
                this.material = null;
                this.materializeMaterial = null;
            } else {
                this.material = getReference(Material.table, material.getId());
                this.materializeMaterial = material;
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
