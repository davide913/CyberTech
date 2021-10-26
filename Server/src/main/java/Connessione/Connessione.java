package Connessione;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
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


        // Create a Map to store the data we want to set            WRITE NEW DATA
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", "Los Angeles");
        docData.put("state", "CA");
        docData.put("country", "USA");
        docData.put("regions", Arrays.asList("west_coast", "socal"));
        // Add a new document (asynchronously) in collection "cities" with id "LA"
        ApiFuture<DocumentReference> future = db.collection("cities").add(docData);
        // ...
        // future.get() blocks on response
        System.out.println("Update time : " + future.get().getId());

        /*asynchronously update doc, create the document if missing
        Map<String, Object> update = new HashMap<>();
        update.put("capital", false);

        ApiFuture<WriteResult> writeResult =
                db
                        .collection("cities")
                        .document("LA")                 //where
                        .set(update, SetOptions.merge());
// ...
        System.out.println("Update time : " + writeResult.get().getUpdateTime());*/


        // Create an initial document to update
        /*DocumentReference frankDocRef = db.collection("users").document("frank");
        Map<String, Object> initialData = new HashMap<>();
        initialData.put("name", "Frank");
        initialData.put("age", 12);

        Map<String, Object> favorites = new HashMap<>();
        favorites.put("food", "Pizza");
        favorites.put("color", "Blue");
        favorites.put("subject", "Recess");
        initialData.put("favorites", favorites);        //SOTTOCLASSE!!

        ApiFuture<WriteResult> initialResult = frankDocRef.set(initialData);
// Confirm that data has been successfully saved by blocking on the operation
        initialResult.get();

// Update age and favorite color
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 13);
        updates.put("favorites.color", "Red");

// Async update document
        ApiFuture<WriteResult> writeResult = frankDocRef.update(updates);
// ...
        System.out.println("Update time : " + writeResult.get().getUpdateTime());*/

        System.out.println("fatto");
    }

}
