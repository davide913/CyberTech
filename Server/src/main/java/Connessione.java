import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

}
