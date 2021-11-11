package Connection;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Connection {
    private static Connection instance = null;

    private Connection() throws IOException {

        FileInputStream serviceAccount =
                new FileInputStream("key/cybertech-a61ab-firebase-adminsdk-ldztw-c537145893.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://cybertech-a61ab-default-rtdb.europe-west1.firebasedatabase.app")
                .build();


        FirebaseApp.initializeApp(options);

    }

    public static void initializeConnection() throws IOException {
        if (instance == null)
            instance = new Connection();
    }

    public static DocumentReference getReference(String table, String id){
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        return db.collection(table).document(id);
    }

    public static DocumentSnapshot getDocument(DocumentReference reference) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> val = reference.get();
        return val.get();
    }


}
