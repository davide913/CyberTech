package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Database;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;

public class Device {
    public final static String table = "device";
    private Timestamp lastUsed;
    private String token;
    private String deviceId;
    private String userId;
    private String id;

    public Device(){}

    private Device(Timestamp lastUsed, String token, String deviceId, String userId, String id) {
        this.lastUsed = lastUsed;
        this.token = token;
        this.deviceId = deviceId;
        this.userId = userId;
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

    private String getUserId() {
        return userId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    protected static Device getDeviceById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Device device = null;

        if (document.exists()) {
            device = document.toObject(Device.class);
            device.setId(document.getId());

            return device;
        } else
            throw new NoDeviceFoundException("No device found with this id: " + id);
    }

    private static Device getDevice(String deviceId, String userId) throws ExecutionException, InterruptedException {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection(table)
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("userId", userId).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoDeviceFoundException("No device found with this deviceId: " + deviceId);

        Device device = documents.get(0).toObject(Device.class);
        device.id = documents.get(0).getId();

        return device;
    }


    protected static Device createDevice(String token, String deviceId, String userId) throws ExecutionException, InterruptedException {
        try{
            Device device = getDevice(deviceId, userId);

            device.updateToken(token);
            device.updateLastUsed();

            return device;
        }
        catch (NoDeviceFoundException e) {
            Timestamp t = new Timestamp(new Date());

            Map<String, Object> myDevice = new HashMap<>();
            myDevice.put("token", token);
            myDevice.put("lastUsed", t);
            myDevice.put("deviceId", deviceId);
            myDevice.put("userId", userId);

            DocumentReference addedDocRef = Database.addToCollection(table, myDevice);

            return new Device(t, token, deviceId, userId, addedDocRef.getId());
        }
    }

    private Task<Void> deleteDeviceAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            return deleteFromCollectionAsync(table, this.id);
        else
            throw new NoDeviceFoundException("No device found with this id: " + id);
    }

    protected boolean deleteDevice() {
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
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("lastUsed", timestamp);
        }
        throw new NoDeviceFoundException("Device not found with this id: "+ id);
    }

    public boolean updateLastUsed() {
        try {
            Timestamp timestamp = new Timestamp(new Date());
            Task<Void> t = this.updateLastUsedAsync(timestamp);
            Tasks.await(t);
            this.lastUsed = timestamp;
            return true;
        } catch (ExecutionException | InterruptedException | NoDeviceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateTokenAsync(@NonNull String token) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("token", token);
        }
        throw new NoDeviceFoundException("Device not found with this id: "+ id);
    }

    public boolean updateToken(@NonNull String token) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
