package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getInstance;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
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
    private Timestamp lastUsed;
    private String token;
    private String deviceId;
    private String id;

    public Device(){}

    public Device(Timestamp lastUsed, String token, String deviceId, String id) {
        this.lastUsed = lastUsed;
        this.token = token;
        this.deviceId = deviceId;
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    private void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Timestamp getLastUsed() {
        return lastUsed;
    }

    private void setLastUsed(Timestamp lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
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

    private static Device getDeviceByDeviceId(String deviceId) throws ExecutionException, InterruptedException {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("device")
                .whereEqualTo("deviceId", deviceId).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoDeviceFoundException("No device found with this deviceId: " + deviceId);

        Device device = documents.get(0).toObject(Device.class);
        device.id = documents.get(0).getId();

        return device;
    }

    public static Device createDevice(String token, String deviceId) throws ExecutionException, InterruptedException {
        Timestamp t = new Timestamp(new Date());
        try{
            Device device = getDeviceByDeviceId(deviceId);

            device.updateToken(token);
            device.updateLastUsed(t.toDate());

            return device;
        }
        catch (NoDeviceFoundException e) {

            Map<String, Object> myDevice = new HashMap<>();
            myDevice.put("token", token);
            myDevice.put("lastUsed", t);
            myDevice.put("deviceId", deviceId);

            DocumentReference addedDocRef = Database.addToCollection("device", myDevice);

            return new Device(t, token, deviceId, addedDocRef.getId());
        }
    }

    //TODO da sistemare
    private Task<Void> deleteDeviceAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("device", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            return deleteFromCollectionAsync("device", this.id);
        else
            throw new NoDeviceFoundException("No device found with this id: " + id);
    }

    public boolean deleteDevice() {
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

    private Task<Void> updateLastUsedAsync(Timestamp timestamp) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("device", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("lastUsed", timestamp);
        }
        throw new NoDeviceFoundException("Device not found with this id: "+ id);
    }

    public boolean updateLastUsed(@NonNull Date date) {
        try {
            Timestamp timestamp = new Timestamp(date);
            Task<Void> t = this.updateLastUsedAsync(timestamp);
            Tasks.await(t);
            this.lastUsed = timestamp;
            return true;
        } catch (ExecutionException | InterruptedException | NoAssistanceTypeFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateTokenAsync(@NonNull String token) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("device", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("token", token);
        }
        throw new NoDeviceFoundException("Device not found with this id: "+ id);
    }

    protected boolean updateToken(@NonNull String token) {
        try {
            Task<Void> t = this.updateTokenAsync(token);
            Tasks.await(t);
            this.token = token;
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
            return d.deviceId.equals(this.deviceId);
        }
        return false;
    }
}
