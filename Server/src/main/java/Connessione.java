import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Connessione {

    public Connessione() throws IOException {


        FileInputStream serviceAccount =
                new FileInputStream("key/cybertech-a61ab-firebase-adminsdk-ldztw-c537145893.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://cybertech-a61ab-default-rtdb.europe-west1.firebasedatabase.app")
                .build();


        FirebaseApp.initializeApp(options);

    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Connessione c = new Connessione();

        Firestore db = FirestoreClient.getFirestore();


        /* Create a Map to store the data we want to set            WRITE NEW DATA
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", "Los Angeles");
        docData.put("state", "CA");
        docData.put("country", "USA");
        docData.put("regions", Arrays.asList("west_coast", "socal"));
        // Add a new document (asynchronously) in collection "cities" with id "LA"
        ApiFuture<WriteResult> future = db.collection("cities").document("LA").set(docData);
        // ...
        // future.get() blocks on response
        //System.out.println("Update time : " + future.get().getUpdateTime());*/

        //asynchronously update doc, create the document if missing
        Map<String, Object> update = new HashMap<>();
        update.put("capital", true);

        ApiFuture<WriteResult> writeResult =
                db
                        .collection("cities")
                        .document("BJ")
                        .set(update, SetOptions.merge());
// ...
        System.out.println("Update time : " + writeResult.get().getUpdateTime());

        System.out.println("fatto");
    }

}
