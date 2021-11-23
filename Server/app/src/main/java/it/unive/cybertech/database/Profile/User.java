package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.cloud.Timestamp;


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;


enum Sex{
    male,
    female,
    nonBinary
}

//TODO all the function are tested
public class User {
    private String id;
    private String name;
    private String surname;
    private Sex sex;
    private String address;
    private String city;
    private String country;
    private GeoPoint position;
    private boolean greenPass;
    private Timestamp positiveSince;
    private long lendingPoint;
    private ArrayList<Device> devices;
    private ArrayList<LendingInProgress> lendingInProgresses;
    private ArrayList<ExtensionRequest> extensionRequests;
    private ArrayList<RentMaterial> rentMaterials;
    private QuarantineAssistance assistance;

    public User() {
    }

    private User(String id, String name, String surname, Sex sex, String address, String city,
                 String country, GeoPoint position, boolean greenPass, Timestamp positiveSince, long lendingPoint) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.address = address;
        this.city = city;
        this.country = country;
        this.position = position;
        this.greenPass = greenPass;
        this.positiveSince = positiveSince;
        this.lendingPoint = lendingPoint;
    }

    private User(String id, String name, String surname, Sex sex, String address,
                 String city, String country, GeoPoint position, boolean greenPass,
                 Timestamp positiveSince, long lendingPoint, ArrayList<Device> devices,
                 ArrayList<LendingInProgress> lendingInProgresses, ArrayList<ExtensionRequest> extensionRequest,
                 ArrayList<RentMaterial> rentMaterials, QuarantineAssistance assistance) {

        this.id = id;
        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.address = address;
        this.city = city;
        this.country = country;
        this.position = position;
        this.greenPass = greenPass;
        this.positiveSince = positiveSince;
        this.lendingPoint = lendingPoint;
        this.devices = devices;
        this.lendingInProgresses = lendingInProgresses;
        extensionRequests = extensionRequest;
        this.rentMaterials = rentMaterials;
        this.assistance = assistance;
    }

    public String getId() {
        return this.id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    private void setSurname(String surname) {
        this.surname = surname;
    }

    public Sex getSex() {
        return sex;
    }

    private void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    private void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    private void setCountry(String country) {
        this.country = country;
    }

    public GeoPoint getPosition() {
        return position;
    }

    private void setPosition(GeoPoint position) {
        this.position = position;
    }

    public boolean isGreenPass() {
        return greenPass;
    }

    private void setGreenPass(boolean greenPass) {
        this.greenPass = greenPass;
    }

    public Timestamp getPositiveSince() {
        return positiveSince;
    }

    private void setPositiveSince(Timestamp positiveSince) {
        this.positiveSince = positiveSince;
    }

    public long getLendingPoint() {
        return lendingPoint;
    }

    private void setLendingPoint(long lendingPoint) {
        this.lendingPoint = lendingPoint;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    private void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    public ArrayList<LendingInProgress> getLendingInProgresses() {
        return lendingInProgresses;
    }

    private void setLendingInProgresses(ArrayList<LendingInProgress> lendingInProgresses) {
        this.lendingInProgresses = lendingInProgresses;
    }

    public ArrayList<ExtensionRequest> getExtensionRequests() {
        return extensionRequests;
    }

    private void setExtensionRequests(ArrayList<ExtensionRequest> extensionRequests) {
        this.extensionRequests = extensionRequests;
    }

    public ArrayList<RentMaterial> getRentMaterials() {
        return rentMaterials;
    }

    private void setRentMaterials(ArrayList<RentMaterial> rentMaterials) {
        this.rentMaterials = rentMaterials;
    }

    public QuarantineAssistance getAssistance() {
        return assistance;
    }

    private void setAssistance(QuarantineAssistance assistance) {
        this.assistance = assistance;
    }

    //The setter are private just for don't permit to the library user to change the value. Firebase library needs setters!

    public static User createUser(String id, String name, String surname, Sex sex, String address,
                                  String city, String country, long latitude, long longitude, boolean greenpass) throws ExecutionException, InterruptedException {

        if(sex != Sex.female && sex != Sex.male && sex != Sex.nonBinary)
            throw new NoUserFoundException("for create a user, the sex need to be male, female or nonBinary");

        Firestore db = FirestoreClient.getFirestore();

        User user = new User(id, name, surname, sex, address, city, country, new GeoPoint(latitude, longitude),
                greenpass, null, 0, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), null);

        ApiFuture<WriteResult> future = db.collection("users").document(id).set(user);
        future.get();

        return user;
    }

    public static User getUserById(String id) throws Exception {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);
            user.setId(document.getId());

            return user;
        } else
            throw new NoUserFoundException("No user found with this id: " + id);

    }

    public Task<Void> deleteUserAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync("users", id);//db.collection("users").document(Id).delete();
        else
            throw new NoUserFoundException("No user found with this id: " + id);
    }

    public boolean deleteUser() {
        try {
            Task<Void> t = deleteUserAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> updateGreenPassAsync(boolean val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Greenpass", val);
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean updateGreenPass(boolean val) {
        try {
            Task<Void> t = updateGreenPassAsync(val);
            Tasks.await(t);
            this.greenPass = val;
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> updatePositiveSinceAsync(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (date != null) {                    //if the date is null is possible to delete the field date from db
                Timestamp timestamp = Timestamp.of(date);//new Timestamp(date);            //conversion from date to timestamp
                return docRef.update("PositiveSince", timestamp);
            } else {
                return docRef.update("PositiveSince", FieldValue.delete());
            }
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    //TODO controllare se lavora la funzione
    public boolean updatePositiveSince(Date date) {
        try {
            Task<Void> t = updatePositiveSinceAsync(date);
            Tasks.await(t);
            this.positiveSince = date == null ? null : Timestamp.of(date);//new Timestamp(date);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> updateLendingPointAsync(long val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists() && val >= 0)
            return docRef.update("LendingPoint", val);
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean updateLendingPoint(long val) {
        try {
            Task<Void> t = updateLendingPointAsync(val);
            Tasks.await(t);
            this.lendingPoint = val;
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> addDeviceAsync(@NonNull Device device) throws Exception {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("Devices", FieldValue.arrayUnion(device));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addDevice(@NonNull Device device) throws Exception {
        try {
            Task<Void> t = addDeviceAsync(device);
            Tasks.await(t);
            this.devices.add(device);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> removeDeviceAsync(@NonNull Device device) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Devices", FieldValue.arrayRemove(device));
            //this.devices.remove(device);
            //return true;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeDevice(@NonNull Device device) {
        try {
            Task<Void> t = removeDeviceAsync(device);
            Tasks.await(t);
            this.devices.remove(device);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDevice(@NonNull Device oldDevice, @NonNull Device newDevice) throws Exception {
        if (!oldDevice.equals(newDevice)) {          //if old and new device are different
            boolean flag = false;

            for (Device d : devices) {               //check if the old device is present in the list of that user
                if (d.equals(oldDevice)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {             //if find the old device it can update that
                removeDevice(oldDevice);
                addDevice(newDevice);
                return true;
            }
        }
        return false;
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> addLendingAsync(@NonNull LendingInProgress lending) throws Exception {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("LendingInProgress", FieldValue.arrayUnion(lending));
            //this.lendingInProgresses.add(lending);
            //return true;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addLending(@NonNull LendingInProgress lending) throws Exception {
        try {
            Task<Void> t = addLendingAsync(lending);
            Tasks.await(t);
            this.lendingInProgresses.add(lending);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> removeLendingAsync(@NonNull LendingInProgress lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("LendingInProgress", FieldValue.arrayRemove(lending));
            //this.lendingInProgresses.remove(lending);
            //return true;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeLending(@NonNull LendingInProgress lending) {
        try {
            Task<Void> t = removeLendingAsync(lending);
            Tasks.await(t);
            this.lendingInProgresses.remove(lending);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLending(@NonNull LendingInProgress oldLending, @NonNull LendingInProgress newLending) throws Exception {
        if (!oldLending.equals(newLending)) {          //if old and new device are different
            boolean flag = false;

            for (LendingInProgress l : lendingInProgresses) {               //check if the old LendingInProgress is present in the list of that user
                if (l.equals(oldLending)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {             //if find the old LendingInProgress it can update that
                removeLending(oldLending);
                addLending(newLending);
                return true;
            }
        }
        return false;
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> addExtensionRequestAsync(@NonNull ExtensionRequest extensionRequest) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("ExtensionRequest", FieldValue.arrayUnion(extensionRequest));
            //this.extensionRequests.add(extensionRequest);
            //return true;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addExtensionRequest(@NonNull ExtensionRequest extensionRequest) {
        try {
            Task<Void> t = addExtensionRequestAsync(extensionRequest);
            Tasks.await(t);
            this.extensionRequests.add(extensionRequest);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> removeExtensionRequestAsync(@NonNull ExtensionRequest extensionRequest) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);
        if (document.exists()) {
            return docRef.update("ExtensionRequest", FieldValue.arrayRemove(extensionRequest));
            //this.extensionRequests.remove(extensionRequest);
            //return true;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }


    public boolean removeExtensionRequest(@NonNull ExtensionRequest extensionRequest) {
        try {
            Task<Void> t = removeExtensionRequestAsync(extensionRequest);
            Tasks.await(t);
            this.extensionRequests.remove(extensionRequest);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExtensionRequest(@NonNull ExtensionRequest oldextensionRequest, @NonNull ExtensionRequest newextensionRequest) throws Exception {
        if (!oldextensionRequest.equals(newextensionRequest)) {
            boolean flag = false;

            for (ExtensionRequest l : extensionRequests) {               //check if the old ExtensionRequest is present in the list of that user
                if (l.equals(oldextensionRequest)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {             //if find the old ExtensionRequest it can update that
                removeExtensionRequest(oldextensionRequest);
                addExtensionRequest(newextensionRequest);
                return true;
            }
        }
        return false;
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> addRentMaterialAsync(@NonNull RentMaterial rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("RentMaterial", FieldValue.arrayUnion(rentMaterial));
            //this.rentMaterials.add(rentMaterial);
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addRentMaterial(@NonNull RentMaterial rentMaterial) {
        try {
            Task<Void> t = addRentMaterialAsync(rentMaterial);
            Tasks.await(t);
            this.rentMaterials.add(rentMaterial);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> removeRentMaterialAsync(@NonNull RentMaterial rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);
        if (document.exists()) {
            //this.rentMaterials.remove(rentMaterial);
            return docRef.update("RentMaterial", FieldValue.arrayRemove(rentMaterial));
            //return true;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeRentMaterial(@NonNull RentMaterial rentMaterial) {
        try {
            Task<Void> t = removeRentMaterialAsync(rentMaterial);
            Tasks.await(t);
            this.rentMaterials.remove(rentMaterial);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRentMaterial(@NonNull RentMaterial oldrentMaterial, @NonNull RentMaterial newrentMaterial) throws Exception {
        if (!oldrentMaterial.equals(newrentMaterial)) {
            boolean flag = false;

            for (RentMaterial l : rentMaterials) {               //check if the old RentMaterial is present in the list of that user
                if (l.equals(oldrentMaterial)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {             //if find the old RentMaterial it can update that
                removeRentMaterial(oldrentMaterial);
                addRentMaterial(newrentMaterial);
                return true;
            }
        }
        return false;
    }

    public Task<Void> updateQuarantineAsync(QuarantineAssistance quarantineAssistance) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);
        Task<Void> t;
        if (document.exists()) {
            if (quarantineAssistance != null) {                  //if the quarantineAssistance is null is possible to delete the field date from db
                t = docRef.update("quarantineAssistance", quarantineAssistance);
                this.assistance = quarantineAssistance;
            } else {
                t = docRef.update("quarantineAssistance", FieldValue.delete());
                this.assistance = null;
            }
            return t;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean updateQuarantine(QuarantineAssistance quarantineAssistance) throws ExecutionException, InterruptedException {
        Task<Void> t = updateQuarantineAsync(quarantineAssistance);
        try {
            Tasks.await(t);
            return true;
        } catch (Exception a) {
            return false;
        }
    }
}
