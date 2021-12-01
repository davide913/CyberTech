package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getInstance;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;

public class Device {
    private String name;
    private String id;

    public Device(){}

    public Device(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    protected static Device getDeviceById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("device", id);
        DocumentSnapshot document = getDocument(docRef);

        Device device = null;

        if (document.exists()) {
            device = document.toObject(Device.class);
            device.setId(document.getId());

            return device;
        } else
            throw new NoDeviceFoundException("No device found with this id: " + id);
    }

    public static Device getDevice(String name) throws ExecutionException, InterruptedException {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("device").whereEqualTo("name", name).get();
        // future.get() blocks on response
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoDeviceFoundException("No device found with this name: " + name);

        Device device = documents.get(0).toObject(Device.class);
        device.id = documents.get(0).getId();

        return device;
    }

    public static Device createDevice(String name) throws ExecutionException, InterruptedException {
        try{
            return getDevice(name);
        }
        catch (NoDeviceFoundException e) {
            Map<String, Object> myDevice = new HashMap<>();
            myDevice.put("name", name);

            DocumentReference addedDocRef = Database.addToCollection("device", myDevice);

            return new Device(name, addedDocRef.getId());
        }
    }

    public Task<Void> deleteDeviceAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("device", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            return deleteFromCollectionAsync("device", this.id);
        else
            throw new NoDeviceFoundException("No device found with this id: " + id);
    }

    public boolean deleteAssistanceType() {
        try {
            Task<Void> t = this.deleteDeviceAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoDeviceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Task<Void> updateDeviceNameAsync(@NonNull String new_name) throws ExecutionException, InterruptedException {
        if (!this.name.equals(new_name) && getDevice(new_name) == null) {
            DocumentReference docRef = getReference("device", this.id);
            DocumentSnapshot document = getDocument(docRef);

            if (document.exists()) {
                return docRef.update("name", new_name);
            }
            throw new NoDeviceFoundException("Update can't work if the device is not saved in the db");
        }
        throw new NoAssistanceTypeFoundException("same name or this device already exist");
    }

    public boolean updateAssistanceType(@NonNull String new_name) {
        try {
            Task<Void> t = this.updateDeviceNameAsync(new_name);
            Tasks.await(t);
            this.name = new_name;
            return true;
        } catch (ExecutionException | InterruptedException | NoAssistanceTypeFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Device) {
            Device d = (Device) o;
            return d.id.equals(this.id);
        }
        return false;
    }
}
