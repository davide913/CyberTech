package it.unive.CyberTech;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Connection {
    FileInputStream serviceAccount =
            new FileInputStream("key/cybertech-a61ab-firebase-adminsdk-ldztw-c537145893.json");

    FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://cybertech-a61ab-default-rtdb.europe-west1.firebasedatabase.app")
            .build();

    public Connection() throws FileNotFoundException {
    }

    FirebaseApp.initializeApp(options);
}
