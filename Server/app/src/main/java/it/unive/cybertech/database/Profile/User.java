package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;
import static it.unive.cybertech.database.Profile.Device.createDevice;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Geoquerable;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;


public class User extends Geoquerable {
    private final static String table = "users";
    private String id;
    private String name;
    private String surname;
    private Sex sex;
    private Timestamp birthday;
    private String address;
    private String city;
    private String country;
    private GeoPoint location;
    private String geohash;
    private boolean greenPass;
    private Timestamp positiveSince;
    private long lendingPoint;
    private ArrayList<DocumentReference> devices;
    private ArrayList<DocumentReference> lendingInProgresses;
    private ArrayList<DocumentReference> materials;
    private DocumentReference quarantineAssistance;

    public User() {}

    private User(String id, String name, String surname, Sex sex, Timestamp birthday,
                 String address, String city, String country, GeoPoint location, boolean greenPass,
                 Timestamp positiveSince, long lendingPoint, ArrayList<DocumentReference> devices,
                 ArrayList<DocumentReference> lendingInProgresses, ArrayList<DocumentReference> materials,
                 DocumentReference quarantineAssistance) {

        this.id = id;
        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
        this.city = city;
        this.country = country;
        this.location = location;
        this.geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
        this.greenPass = greenPass;
        this.positiveSince = positiveSince;
        this.lendingPoint = lendingPoint;
        this.devices = devices;
        this.lendingInProgresses = lendingInProgresses;
        this.materials = materials;
        this.quarantineAssistance = quarantineAssistance;
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

    public GeoPoint getLocation() {
        return location;
    }

    private void setLocation(GeoPoint location) {
        this.location = location;
    }

    private String getGeohash() {
        return geohash;
    }

    private void setGeohash(String geohash) {
        this.geohash = geohash;
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

    public ArrayList<DocumentReference> getDevices() {
        return devices;
    }

    private void setDevices(ArrayList<DocumentReference> devices) {
        this.devices = devices;
    }

    public ArrayList<DocumentReference> getLendingInProgresses() {
        return lendingInProgresses;
    }

    private void setLendingInProgresses(ArrayList<DocumentReference> lendingInProgresses) {
        this.lendingInProgresses = lendingInProgresses;
    }

    public ArrayList<DocumentReference> getMaterials() {
        return materials;
    }

    private void setMaterials(ArrayList<DocumentReference> materials) {
        this.materials = materials;
    }

    public DocumentReference getQuarantineAssistance() {
        return quarantineAssistance;
    }

    private void setQuarantineAssistance(DocumentReference quarantineAssistance) {
        this.quarantineAssistance = quarantineAssistance;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public Date getBirthDayToDate() {
        return birthday.toDate();
    }

    private void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public ArrayList<Device> getMaterializedDevices() throws ExecutionException, InterruptedException {
        ArrayList<Device> arr = new ArrayList<>();

        for (DocumentReference doc : devices) {
            arr.add(Device.getDeviceById(doc.getId()));
        }

        return arr;
    }

    public ArrayList<LendingInProgress> getMaterializedLendingInProgress() throws ExecutionException, InterruptedException {
        ArrayList<LendingInProgress> arr = new ArrayList<>();

        for (DocumentReference doc : lendingInProgresses) {
            arr.add(LendingInProgress.getLendingInProgressById(doc.getId()));
        }

        return arr;
    }

    public ArrayList<Material> getMaterializedUserMaterials() throws ExecutionException, InterruptedException {
        ArrayList<Material> arr = new ArrayList<>();

        for (DocumentReference doc : materials) {
            arr.add(Material.getMaterialById(doc.getId()));
        }

        return arr;
    }

    public QuarantineAssistance getMaterializedQuarantineAssistance() throws ExecutionException, InterruptedException {
        return QuarantineAssistance.getQuarantineAssistanceById(quarantineAssistance.getId());
    }

    //modificata il 13/12/2021 -> aggiunta la data di compleanno
    public static User createUser(String id, String name, String surname, Sex sex, Date birthDay,
                                  String address, String city, String country, long latitude, long longitude,
                                  boolean greenpass) throws ExecutionException, InterruptedException {

        if(sex != Sex.female && sex != Sex.male && sex != Sex.nonBinary)
            throw new NoUserFoundException("for create a user, the sex need to be male, female or nonBinary");

        User user = new User(id, name, surname, sex, new Timestamp(birthDay), address, city, country, new GeoPoint(latitude, longitude),
                greenpass, null, 0, new ArrayList<DocumentReference>(),
                new ArrayList<DocumentReference>(),
                new ArrayList<DocumentReference>(), null);

        Task<Void> future = getInstance().collection(table).document(id).set(user);
        Tasks.await(future);

        return user;
    }

    //TODO verificare che se ci sono degli array a null di inizializzarli! ( in caso cotrario le update dei rispettivi array andrebbero in errore )
    public static User getUserById(String id) throws  InterruptedException, ExecutionException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);
            user.setId(document.getId());

            if(user.devices == null)
                user.devices = new ArrayList<>();

            if(user.lendingInProgresses == null)
                user.lendingInProgresses = new ArrayList<>();

            if(user.materials == null)
                user.materials = new ArrayList<>();

            return user;
        } else
            throw new NoUserFoundException("No user found with this id: " + id);
    }

    private Task<Void> deleteUserAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
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
    //modificata 30/11/2021, greenpass era maiuscolo
    private Task<Void> updateGreenPassAsync(boolean val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("greenPass", val);
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean updateGreenPass() {
        try {
            Task<Void> t = updateGreenPassAsync(!this.greenPass);
            Tasks.await(t);
            this.greenPass = !this.greenPass;
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> updatePositiveSinceAsync(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (date != null) {                    //if the date is null is possible to delete the field date from db
                Timestamp timestamp = new Timestamp(date);            //conversion from date to timestamp
                return docRef.update("positiveSince", timestamp);
            } else {
                return docRef.update("positiveSince", FieldValue.delete());
            }
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean updatePositiveSince(Date date) {
        try {
            Task<Void> t = updatePositiveSinceAsync(date);
            Tasks.await(t);
            this.positiveSince = date == null ? null : new Timestamp(date);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateLocationAsync(String newCountry, String newCity, String newAddress, GeoPoint geoPoint)
            throws ExecutionException, InterruptedException {

        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("address", newAddress);
            docRef.update("city", newCity);
            docRef.update("country", newCountry);
            return docRef.update("location", geoPoint);

        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean updateLocation(String newCountry, String newCity, String newAddress, double latitude, double longitude){
        try {
            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            Task<Void> t = updateLocationAsync(newCountry, newCity, newAddress, geoPoint);
            Tasks.await(t);
            this.country = newCountry;
            this.city = newCity;
            this.address = newAddress;
            this.location = geoPoint;
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Task<Void> updateLendingPointAsync(long val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists() && val >= 0)
            return docRef.update("lendingPoint", val);
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
    private Task<Void> addDeviceAsync(@NonNull DocumentReference device) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("devices", FieldValue.arrayUnion(device));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    public boolean addDevice(@NonNull String token, @NonNull String deviceId) {
        try {
            Device device = createDevice(token, deviceId, this.id);
            DocumentReference devDoc = getReference("devices", device.getId());

            if(notContainDevice(device.getId())) {
                Tasks.await(addDeviceAsync(devDoc));
                this.devices.add(devDoc);
            }
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> removeDeviceAsync(@NonNull DocumentReference device) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("devices", FieldValue.arrayRemove(device));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    public boolean removeDevice(@NonNull Device device) {
        try {
            DocumentReference devDoc = getReference("devices", device.getId());
            Tasks.await(removeDeviceAsync(devDoc));
            this.devices.remove(devDoc);
            device.deleteDevice();
            return true;
        } catch ( NoDeviceFoundException | NoUserFoundException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean notContainDevice(String deviceId){
        for (DocumentReference document: this.devices ) {
            if(deviceId.equals(document.getId()))
                return false;
        }
        return true;
    }

    //modificata 30/11/2021, non salvo la classe intera LendingInProgress ma solo la sua reference sul db
    private Task<Void> addLendingAsync(@NonNull DocumentReference lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("lendingInProgress", FieldValue.arrayUnion(lending));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addLending(@NonNull LendingInProgress lending) {
        try {
            DocumentReference lenDoc = getReference("lendingInProgress", lending.getId());
            Task<Void> t = addLendingAsync(lenDoc);
            Tasks.await(t);
            this.lendingInProgresses.add(lenDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //modificata 30/11/2021, non salvo la classe intera LendingInProgress ma solo la sua reference sul db
    private Task<Void> removeLendingAsync(@NonNull DocumentReference lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("lendingInProgress", FieldValue.arrayRemove(lending));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeLending(@NonNull LendingInProgress lending) {
        try {
            DocumentReference lenDoc = getReference("lendingInProgress", lending.getId());
            Task<Void> t = removeLendingAsync(lenDoc);
            Tasks.await(t);
            this.lendingInProgresses.remove(lenDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //modificata 30/11/2021, non salvo la classe intera RentMaterial ma solo la sua reference sul db
    private Task<Void> addMaterialAsync(@NonNull DocumentReference rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("materials", FieldValue.arrayUnion(rentMaterial));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addMaterial(@NonNull Material material) {
        try {
            DocumentReference rentDoc = getReference("material", material.getId());
            Task<Void> t = addMaterialAsync(rentDoc);
            Tasks.await(t);
            this.materials.add(rentDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //modificata 30/11/2021, non salvo la classe intera RentMaterial ma solo la sua reference sul db
    private Task<Void> removeMaterialAsync(@NonNull DocumentReference rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("materials", FieldValue.arrayRemove(rentMaterial));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeMaterial(@NonNull Material material) {
        try {
            DocumentReference rentDoc = getReference("material", material.getId());
            Task<Void> t = removeMaterialAsync(rentDoc);
            Tasks.await(t);
            this.materials.remove(rentDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //modificata 30/11/2021, non salvo la classe intera quarantine assistance ma solo la sua reference sul db
    private Task<Void> updateQuarantineAsync(QuarantineAssistance quarantineAssistance) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Task<Void> t;
        if (document.exists()) {
            if (quarantineAssistance != null) {                 //if the quarantineAssistance is null is possible to delete the field date from db
                DocumentReference assDoc = getReference("quarantineAssistance", quarantineAssistance.getId());
                t = docRef.update("quarantineAssistance", assDoc);
            }
            else
                t = docRef.update("quarantineAssistance", FieldValue.delete());
            return t;
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean updateQuarantine(QuarantineAssistance quarantineAssistance) throws ExecutionException, InterruptedException {
        try {
            Task<Void> t = updateQuarantineAsync(quarantineAssistance);
            Tasks.await(t);
            if(quarantineAssistance == null)
                this.quarantineAssistance = null;
            else
                this.quarantineAssistance = getReference("quarantineAssistance", quarantineAssistance.getId());
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //aggiunta il 7/12/2021
    public static List<LendingInProgress> getUserLandingsInProgress(String id) throws ExecutionException, InterruptedException {
        User user = getUserById(id);

        return user.getMaterializedLendingInProgress();
    }

    //aggiunta il 7/12/2021
    public static List<Device> getUserDevices(String id) throws ExecutionException, InterruptedException {
        User user = getUserById(id);

        return user.getMaterializedDevices();
    }


    //metodo equal per confronti
    @Override
    public boolean equals(Object o){
        if(o instanceof User){
            User user = (User)o;

            return user.getId().equals(this.getId());
        }
        return false;
    }
}
