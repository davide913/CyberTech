package it.unive.cybertech.database.Connection;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Database {

    private Database() {
    }

    public static void initialize(Context context) {
        FirebaseApp.initializeApp(context);
    }

    public static FirebaseFirestore getInstance(){
        return FirebaseFirestore.getInstance();
    }

    public static DocumentReference getReference(String table, String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();      //create of object db
        return db.collection(table).document(id);
    }

    public static DocumentSnapshot getDocument(DocumentReference reference)  {
        Task<DocumentSnapshot> val = reference.get();
        return val.getResult();
    }

    public static <K,V>DocumentReference addToCollection(String table, Map<K,V> map) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();      //create of object db
        Task<DocumentReference> insert = db.collection(table).add(map);
        return insert.getResult();
    }

    public static boolean deleteFromCollection(String table, String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();      //create of object db
        db.collection(table).document(id).delete().getResult();
        return true;
    }
}
