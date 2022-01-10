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

/**
 * Class use to describe a user's device instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 *
 * @author Davide Finesso
 */
public class Device {
    public final static String table = "device";
    private Timestamp lastUsed;
    private String token;
    private String deviceId;
    private String userId;
    private String id;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public Device(){}

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
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

    /**
     * The protected method return a device from his id.
     *
     * @author Davide Finesso
     */
    protected static Device obtainDeviceById(String id) throws ExecutionException, InterruptedException {
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

    /**
     * The method return a user's device the user's id and a device's id.
     *
     * @author Davide Finesso
     */
    private static Device obtainDevice(String deviceId, String userId) throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> future = getInstance().collection(table)
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("userId", userId).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoDeviceFoundException("No device found with this deviceId: " + deviceId);

        Device device = documents.get(0).toObject(Device.class);
        device.setId( documents.get(0).getId());

        return device;
    }

    /**
     * The protected method add to the database a new device and return it.
     *
     * @author Davide Finesso
     */
    protected static Device createDevice(String token, String deviceId, String userId) throws ExecutionException, InterruptedException {
        try{
            Device device = obtainDevice(deviceId, userId);

            device.updateToken(token);
            device.updateLastUsed();

            return device;
        }
        catch (NoDeviceFoundException e) {
            Timestamp t = Timestamp.now();

            Map<String, Object> myDevice = new HashMap<>();
            myDevice.put("token", token);
            myDevice.put("lastUsed", t);
            myDevice.put("deviceId", deviceId);
            myDevice.put("userId", userId);

            DocumentReference addedDocRef = Database.addToCollection(table, myDevice);

            return new Device(t, token, deviceId, userId, addedDocRef.getId());
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> deleteDeviceAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            return deleteFromCollectionAsync(table, this.id);
        else
            throw new NoDeviceFoundException("No device found with this id: " + id);
    }

    /**
     * The protected method is use to delete a device to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    protected boolean deleteDevice() {
        try {
            Task<Void> t = this.deleteDeviceAsync();
            Tasks.await(t);
            this.setId( null );
            return true;
        } catch (ExecutionException | InterruptedException | NoDeviceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateLastUsedAsync(Timestamp timestamp) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("lastUsed", timestamp);
        }
        throw new NoDeviceFoundException("Device not found with this id: "+ id);
    }

    /**
     * The method is use to update a device's field last used to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateLastUsed() {
        try {
            Timestamp timestamp = Timestamp.now();
            Task<Void> t = this.updateLastUsedAsync(timestamp);
            Tasks.await(t);
            this.setLastUsed( timestamp );
            return true;
        } catch (ExecutionException | InterruptedException | NoDeviceFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateTokenAsync(@NonNull String token) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("token", token);
        }
        throw new NoDeviceFoundException("Device not found with this id: "+ id);
    }

    /**
     * The method is use to update a device's field token to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateToken(@NonNull String token) {
        try {
            Task<Void> t = this.updateTokenAsync(token);
            Tasks.await(t);
            this.setToken( token );
            return true;
        } catch (ExecutionException | InterruptedException | NoAssistanceTypeFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compare their id because are unique.
     *
     * @author Davide Finesso
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    /**
     * Return the hash by the unique field id.
     *
     * @author Davide Finesso
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
