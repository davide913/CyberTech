package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;

/**
 * Class use to describe a user's device instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 * firebase required a get and set to serialize and deserialize the object; for don't mix our "getter" with the firebase deserialization we call the method obtain
 *
 * @author Davide Finesso
 */
public class AssistanceType {
    public final static String table = "assistanceType";
    private String type;
    private String id;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public AssistanceType() {}

    public String getId() {
        return id;
    }

    private void setId(String ID) {
        this.id = ID;
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * The method return the assistance type with that id. If there isn't a assistance type with that id it throw an exception.
     *
     * @author Davide Finesso
     * @throws NoAssistanceTypeFoundException if a assistance type with that id doesn't exist
     */
    protected static AssistanceType obtainAssistanceTypeById(@NonNull String id) throws  InterruptedException, ExecutionException, NoAssistanceTypeFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        AssistanceType assistanceType = null;

        if (document.exists()) {
            assistanceType = document.toObject(AssistanceType.class);
            assistanceType.setId(document.getId());

            return assistanceType;
        } else
            throw new NoAssistanceTypeFoundException("No Assistance Type found with this id: " + id);
    }

    /**
     * The method return all the assistance type present in the database.
     *
     * @author Davide Finesso
     */
    public static List<AssistanceType> obtainAssistanceTypes() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> future = getInstance().collection(table).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        List<AssistanceType> arr = new ArrayList<>();
        for (DocumentSnapshot snapshot: documents)
            arr.add(obtainAssistanceTypeById(snapshot.getId()));

        return arr;
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
        AssistanceType that = (AssistanceType) o;
        return Objects.equals(id, that.id);
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

    /**
     * Return the toString of an assistance type as his type.
     *
     * @author Davide Finesso
     */
    @Override
    public String toString() {
        return type;
    }
}
