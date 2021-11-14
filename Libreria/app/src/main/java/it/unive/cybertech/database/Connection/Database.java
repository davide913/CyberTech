package it.unive.cybertech.database.Connection;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Database {

    private Database() {
    }

    public static void initialize(Context context) {
        FirebaseApp.initializeApp(context);
    }

    public static FirebaseFirestore getInstance() {
        return FirebaseFirestore.getInstance();
    }

    public static DocumentReference getReference(String table, String id) {
        FirebaseFirestore db = getInstance();
        return db.collection(table).document(id);
    }

    public static Task<DocumentSnapshot> getDocumentAsync(String table, String id) {
        return getReference(table, id).get();
    }

    public static Task<DocumentSnapshot> getDocumentAsync(DocumentReference reference) {
        return reference.get();
    }

    public static DocumentSnapshot getDocument(DocumentReference reference) throws InterruptedException, ExecutionException {
        Task<DocumentSnapshot> val = getDocumentAsync(reference);
        Tasks.await(val);
        return val.getResult();
    }

    public static DocumentSnapshot getDocument(String table, String id) throws InterruptedException, ExecutionException {
        Task<DocumentSnapshot> val = getDocumentAsync(table, id);
        Tasks.await(val);
        return val.getResult();
    }

    public static <K, V> Task<DocumentReference> addToCollectionAsync(String table, Map<K, V> map) {
        FirebaseFirestore db = getInstance();
        return db.collection(table).add(map);
    }

    public static <K, V> DocumentReference addToCollection(String table, Map<K, V> map) throws InterruptedException, ExecutionException {
        Task<DocumentReference> t = addToCollectionAsync(table, map);
        Tasks.await(t);
        return t.getResult();
    }

    public static Task<Void> deleteFromCollectionAsync(String table, String id) {
        FirebaseFirestore db = getInstance();//create of object db
        return db.collection(table).document(id).delete();
    }

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
