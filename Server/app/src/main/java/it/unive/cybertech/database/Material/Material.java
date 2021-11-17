package it.unive.cybertech.database.Material;

import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.getInstance;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Device;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;

public class Material {
    private String id;
    private String name;

    public Material() {
    }

    public Material(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public static Material addMaterial(String name) throws ExecutionException, InterruptedException {

        Map<String, Object> myMaterial = new HashMap<>();          //create "table"
        myMaterial.put("name", name);

        DocumentReference addedDocRef = addToCollection("material", myMaterial);

        return new Material(addedDocRef.getId(), name);
    }

    public static Material getMaterial(String name){
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("material").whereEqualTo("name", name).get();
        // future.get() blocks on response
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoDeviceFoundException("No device found with this name: " + name);

        Material material = documents.get(0).toObject(Material.class);
        material.id = documents.get(0).getId();

        return material;
    }
}
