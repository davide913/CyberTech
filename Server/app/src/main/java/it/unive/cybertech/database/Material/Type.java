package it.unive.cybertech.database.Material;

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

import it.unive.cybertech.database.Material.Exception.NoMaterialTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;

/**
 * Class use to describe a material's type instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 *
 * @author Davide Finesso
 */
public class Type {
    public final static String table = "materialType";
    private String typeName;
    private String id;

    public Type(){};

    public String getTypeName() {
        return typeName;
    }

    private void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    /**
     * The private method is use to get the material type by his id.
     * if there isn't any material type with that id it throw an exception
     *
     * @author Davide Finesso
     */
    private static Type obtainMaterialTypeById(String id) throws  InterruptedException, ExecutionException, NoAssistanceTypeFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Type type = null;

        if (document.exists()) {
            type = document.toObject(Type.class);
            type.setId(document.getId());

            return type;
        } else
            throw new NoMaterialTypeFoundException("No Material Type found with this id: " + id);
    }

    /**
     * The method is use to get all the material type from database.
     *
     * @author Davide Finesso
     */
    public static ArrayList<Type> obtainMaterialTypes() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> future = getInstance().collection(table).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        ArrayList<Type> arr = new ArrayList<>();
        for (DocumentSnapshot snapshot: documents)
            arr.add(Type.obtainMaterialTypeById(snapshot.getId()));

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
        Type type = (Type) o;
        return Objects.equals(id, type.id);
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
     * Return the toString of an type material as his type name.
     *
     * @author Davide Finesso
     */
    @NonNull
    @Override
    public String toString() {
        return typeName;
    }
}
