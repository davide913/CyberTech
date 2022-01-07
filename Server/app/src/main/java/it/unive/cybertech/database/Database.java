package it.unive.cybertech.database;


import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Class used to group all the methods refer to the database.
 *
 * @author Davide Finesso
 */
public class Database {

    private Database() {}

    /**
     * This method initialized the app with a specify context.
     *
     * @author Davide Finesso
     */
    public static void initialize(Context context) {
        FirebaseApp.initializeApp(context);
    }

    /**
     * This method get the instance of a database as a singleton.
     *
     * @author Davide Finesso
     */
    public static FirebaseFirestore getInstance() {
        return FirebaseFirestore.getInstance();
    }

    /**
     * This method return the document reference of specify element ( id ) in a table.
     *
     * @author Davide Finesso
     */
    public static DocumentReference getReference(String table, String id) {
        FirebaseFirestore db = getInstance();
        return db.collection(table).document(id);
    }

    /**
     * This private method return a task by the get reference method.
     *
     * @author Davide Finesso
     */
    public static Task<DocumentSnapshot> getDocumentAsync(String table, String id) {
        return getReference(table, id).get();
    }

    /**
     * This private method return a task by the document reference.
     *
     * @author Davide Finesso
     */
    public static Task<DocumentSnapshot> getDocumentAsync(DocumentReference reference) {
        return reference.get();
    }

    /**
     * This method return a document snapshot by the document reference. It wait until the async operation is finish.
     *
     * @author Davide Finesso
     */
    public static DocumentSnapshot getDocument(DocumentReference reference) throws InterruptedException, ExecutionException {
        Task<DocumentSnapshot> val = getDocumentAsync(reference);
        Tasks.await(val);
        return val.getResult();
    }

    /**
     * This method return a document snapshot by a specify element ( id ) in a table. It wait until the async operation is finish.
     *
     * @author Davide Finesso
     */
    public static DocumentSnapshot getDocument(String table, String id) throws InterruptedException, ExecutionException {
        Task<DocumentSnapshot> val = getDocumentAsync(table, id);
        Tasks.await(val);
        return val.getResult();
    }

    /**
     * This method add a map in a table and it return a task that describe it. This is made by an async operation
     *
     * @author Davide Finesso
     */
    public static <K, V> Task<DocumentReference> addToCollectionAsync(String table, Map<K, V> map) {
        FirebaseFirestore db = getInstance();
        return db.collection(table).add(map);
    }

    /**
     * This method return a document reference of a element just add. it wait until the async operation is finish.
     *
     * @author Davide Finesso
     */
    public static <K, V> DocumentReference addToCollection(String table, Map<K, V> map) throws InterruptedException, ExecutionException {
        Task<DocumentReference> t = addToCollectionAsync(table, map);
        Tasks.await(t);
        return t.getResult();
    }

    /**
     * This method remove a element from a table and return a task that describe it. This is made by an async operation
     *
     * @author Davide Finesso
     */
    public static Task<Void> deleteFromCollectionAsync(String table, String id) {
        FirebaseFirestore db = getInstance();
        return db.collection(table).document(id).delete();
    }

    /**
     * This method return delete an element from a table and return true if and only if the operation is done. it wait until the async operation is finish.
     *
     * @author Davide Finesso
     */
    public static boolean deleteFromCollection(String table, String id) {
        Task<Void> t = deleteFromCollectionAsync(table, id);
        try {
            Tasks.await(t);
            return true;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
