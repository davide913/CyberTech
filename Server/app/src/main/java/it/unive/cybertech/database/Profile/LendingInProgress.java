package it.unive.cybertech.database.Profile;


import static it.unive.cybertech.database.Database.addToCollection;
import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.LendingInProgressException;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;

/**
 * Class use to describe a lending in progresses instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 * firebase required a get and set to serialize and deserialize the object; for don't mix our "getter" with the firebase deserialization we call the method obtain
 *
 * @author Davide Finesso
 */
public class LendingInProgress {
    public final static String table = "lendingInProgress";
    private DocumentReference material;
    private Timestamp expiryDate;
    private Timestamp endExpiryDate;
    private boolean waitingForFeedback;
    private String id;

    /**
     * Materialize field for increase the performance.
     */
    private Material materializeMaterial;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public LendingInProgress() {
    }

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
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

    /**
     * The method return the field device materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public Material obtainMaterializedMaterial() throws ExecutionException, InterruptedException {
        if (materializeMaterial == null)
            materializeMaterial = Material.obtainMaterialById(material.getId());
        return materializeMaterial;
    }

    /**
     * The method add to the database a new lending in progress and return it.
     *
     * @author Davide Finesso
     */
    public static LendingInProgress createLendingInProgress(@NonNull Material material,@NonNull Date expiryDate)
            throws ExecutionException, InterruptedException, LendingInProgressException {

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
            throw new LendingInProgressException("A lending with this material is already in progress: " + material.getId());
    }

    /**
     * The method return the lending in progress with that id. If there isn't a quarantine assistance with that id it throw an exception.
     *
     * @author Davide Finesso
     * @throws NoLendingInProgressFoundException if a lending in progress with that id doesn't exist
     */
    public static LendingInProgress obtainLendingInProgressById(@NonNull String id) throws ExecutionException, InterruptedException, NoLendingInProgressFoundException {
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> deleteLendingInProgressAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
        else
            throw new NoLendingInProgressFoundException("No lending in progress found with this id: " + id);
    }

    /**
     * The method is use to delete a lending in progress to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean deleteLendingInProgress() {
        try {
            Task<Void> t = deleteLendingInProgressAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoLendingInProgressFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateExpiryDateAsync(Timestamp date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("expiryDate", date);
        } else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    /**
     * The method is use to update a lending in progress field expiry date to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateWaitingForFeedbackAsync(boolean val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("waitingForFeedback", val);
        } else
            throw new NoLendingInProgressFoundException("No Lending in progress with this id: " + this.id);
    }

    /**
     * The method is use to update a lending in progress field waiting for feedback to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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

    /**
     * The method is use to update a lending in progress field end expiry date to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
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

    /**
     * The method is use to update a lending in progress field material to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * Compare their id because are unique.
     *
     * @author Davide Finesso
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LendingInProgress lending = (LendingInProgress) o;
        return Objects.equals(id, lending.id);
    }

    /**
     * Return the hash by the unique field id.
     *
     * @author Davide Finesso
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
