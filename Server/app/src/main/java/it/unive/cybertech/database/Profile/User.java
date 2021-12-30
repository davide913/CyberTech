package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;
import static it.unive.cybertech.database.Groups.Activity.getActivityById;
import static it.unive.cybertech.database.Profile.Device.createDevice;
import static it.unive.cybertech.database.Profile.QuarantineAssistance.createQuarantineAssistance;

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
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Geoquerable;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;

//TODO una volta rimossa un campo negli arraylist procedere con l'eliminazione di quest'ultimo

public class User extends Geoquerable implements Comparable<User> {
    public final static String table = "users";
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
    private ArrayList<DocumentReference> lendingInProgress;
    private ArrayList<DocumentReference> materials;
    private ArrayList<DocumentReference> quarantineAssistance;

    //aggiunti 17/12/2021 come cache per le materialized
    private ArrayList<Device> devicesMaterialized;
    private ArrayList<LendingInProgress> lendingInProgressMaterialized;
    private ArrayList<Material> materialsMaterialized;
    private ArrayList<QuarantineAssistance> quarantineAssistanceMaterialized;

    public User() {
    }

    private User(String id, String name, String surname, Sex sex, Timestamp birthday,
                 String address, String city, String country, GeoPoint location, boolean greenPass,
                 Timestamp positiveSince, long lendingPoint, ArrayList<DocumentReference> devices,
                 ArrayList<DocumentReference> lendingInProgress, ArrayList<DocumentReference> materials,
                 ArrayList<DocumentReference> quarantineAssistance) {

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
        this.lendingInProgress = lendingInProgress;
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

    public List<DocumentReference> getDevices() {
        return devices;
    }

    private void setDevices(ArrayList<DocumentReference> devices) {
        this.devices = devices;
    }

    public List<DocumentReference> getLendingInProgress() {
        return lendingInProgress;
    }

    private void setLendingInProgress(ArrayList<DocumentReference> lendingInProgress) {
        this.lendingInProgress = lendingInProgress;
    }

    public List<DocumentReference> getMaterials() {
        return materials;
    }

    private void setMaterials(ArrayList<DocumentReference> materials) {
        this.materials = materials;
    }

    public List<DocumentReference> getQuarantineAssistance() {
        return quarantineAssistance;
    }

    private void setQuarantineAssistance(ArrayList<DocumentReference> quarantineAssistance) {
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

    public List<Device> getMaterializedDevices() throws ExecutionException, InterruptedException {
        if (devicesMaterialized == null) {
            devicesMaterialized = new ArrayList<>();

            for (DocumentReference doc : devices)
                //try {
                devicesMaterialized.add(Device.getDeviceById(doc.getId()));
            //}
            //catch (ExecutionException | InterruptedException | NoDeviceFoundException ignored){}
        }

        return devicesMaterialized;
    }

    public List<LendingInProgress> getMaterializedLendingInProgress() throws ExecutionException, InterruptedException {
        if (lendingInProgressMaterialized == null) {
            lendingInProgressMaterialized = new ArrayList<>();
            for (DocumentReference doc : lendingInProgress)
                lendingInProgressMaterialized.add(LendingInProgress.getLendingInProgressById(doc.getId()));
        }
        Timestamp timestamp = Timestamp.now();
        List<LendingInProgress> result = new ArrayList<>();
        for (LendingInProgress lending : lendingInProgressMaterialized) {
            if (timestamp.compareTo(lending.getExpiryDate()) <= 0)
                result.add(lending);
        }
        return result;
    }

    public List<Material> getMaterializedUserMaterials() throws ExecutionException, InterruptedException {
        if (materialsMaterialized == null) {
            materialsMaterialized = new ArrayList<>();
            for (DocumentReference doc : materials)
                materialsMaterialized.add(Material.getMaterialById(doc.getId()));
        }
        Timestamp timestamp = Timestamp.now();
        List<Material> result = new ArrayList<>();
        for (Material material : materialsMaterialized) {
            if (timestamp.compareTo(material.getExpiryDate()) <= 0)
                result.add(material);
        }
        return result;
    }


    public List<QuarantineAssistance> getMaterializedQuarantineAssistance() throws ExecutionException, InterruptedException {
        if (quarantineAssistanceMaterialized == null) {
            quarantineAssistanceMaterialized = new ArrayList<>();

            for (DocumentReference doc : materials)
                quarantineAssistanceMaterialized.add(QuarantineAssistance.getQuarantineAssistanceById(doc.getId()));
        }

        return quarantineAssistanceMaterialized;
    }

    //modificata il 13/12/2021 -> aggiunta la data di compleanno
    public static User createUser(@NonNull String id, @NonNull String name, @NonNull String surname, @NonNull Sex sex, @NonNull Date birthDay,
                                  @NonNull String address, @NonNull String city, @NonNull String country, long latitude, long longitude,
                                  boolean greenpass) throws ExecutionException, InterruptedException {

        if (sex != Sex.female && sex != Sex.male && sex != Sex.nonBinary)
            throw new NoUserFoundException("for create a user, the sex need to be male, female or nonBinary");

        GeoPoint geoPoint = new GeoPoint(latitude, longitude);

        Map<String, Object> myUser = new HashMap<>();
        myUser.put("name", name);
        myUser.put("surname", surname);
        myUser.put("sex", sex);
        myUser.put("birthday", birthDay);
        myUser.put("address", address);
        myUser.put("city", city);
        myUser.put("country", country);
        myUser.put("location", geoPoint);
        myUser.put("geohash", GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude)));
        myUser.put("greenPass", greenpass);
        myUser.put("positiveSince", null);
        myUser.put("lendingPoint", 0);

        Task<Void> future = getInstance().collection(table).document(id).set(myUser);
        Tasks.await(future);

        return new User(id, name, surname, sex, new Timestamp(birthDay), address, city, country,
                geoPoint, greenpass, null, 0,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }


    public static User getUserById(String id) throws InterruptedException, ExecutionException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);
            user.setId(document.getId());

            if (user.devices == null)
                user.devices = new ArrayList<>();

            if (user.lendingInProgress == null)
                user.lendingInProgress = new ArrayList<>();

            if (user.materials == null)
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


    //modificata 30/11/2021, greenpass era maiuscolo
    private Task<Void> updateGreenPassAsync(boolean val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("greenPass", val);
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

    public boolean updateLocation(String newCountry, String newCity, String newAddress, double latitude, double longitude) {
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

        if (document.exists())
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


    private Task<Void> addDeviceAsync(@NonNull DocumentReference device) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update(Device.table, FieldValue.arrayUnion(device));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    public boolean addDevice(@NonNull String token, @NonNull String deviceId) {
        try {
            Device device = createDevice(token, deviceId, this.id);
            DocumentReference devDoc = getReference(Device.table, device.getId());

            if (notContainDevice(device.getId())) {
                Tasks.await(addDeviceAsync(devDoc));
                this.devices.add(devDoc);
                if (this.devicesMaterialized != null)
                    this.getMaterializedDevices().add(device);
            }
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


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
            DocumentReference devDoc = getReference(Device.table, device.getId());
            Tasks.await(removeDeviceAsync(devDoc));
            this.devices.remove(devDoc);
            if (this.devicesMaterialized != null)
                this.getMaterializedDevices().remove(device);
            device.deleteDevice();
            return true;
        } catch (NoDeviceFoundException | NoUserFoundException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    //nuova, inserita il 13/12/2021
    private boolean notContainDevice(String deviceId) {
        for (DocumentReference document : this.devices) {
            if (deviceId.equals(document.getId()))
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
            DocumentReference lenDoc = getReference(LendingInProgress.table, lending.getId());
            Task<Void> t = addLendingAsync(lenDoc);
            Tasks.await(t);
            this.lendingInProgress.add(lenDoc);
            if (this.lendingInProgressMaterialized != null)
                this.getMaterializedLendingInProgress().add(lending);
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
            DocumentReference lenDoc = getReference(LendingInProgress.table, lending.getId());
            Task<Void> t = removeLendingAsync(lenDoc);
            Tasks.await(t);
            this.lendingInProgress.remove(lenDoc);
            if (this.lendingInProgressMaterialized != null)
                this.getMaterializedLendingInProgress().remove(lending);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LendingInProgress> getExpiredLending() throws ExecutionException, InterruptedException {
        ArrayList<LendingInProgress> result = new ArrayList<>();
        Timestamp timestamp = Timestamp.now();

        for (LendingInProgress lending : getMaterializedLendingInProgress()) {
            if (timestamp.compareTo(lending.getExpiryDate()) > 0) //&& !lending.getWaitingForFeedback()
                result.add(lending);
        }

        return result;
    }

    //fatta io sorry <3
    public List<LendingInProgress> getMyMaterialsExpiredLending() throws ExecutionException, InterruptedException {
        ArrayList<LendingInProgress> result = new ArrayList<>();
        if (!materials.isEmpty()) {
            Task<QuerySnapshot> future = getInstance().collection(LendingInProgress.table).whereIn("material", materials).get();
            Tasks.await(future);
            for (DocumentSnapshot ref : future.getResult().getDocuments())
                result.add(LendingInProgress.getLendingInProgressById(ref.getReference().getId()));
        }
        return result;
    }

    //modificata 30/11/2021, non salvo la classe intera RentMaterial ma solo la sua reference sul db
    private Task<Void> addMaterialAsync(@NonNull DocumentReference material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("materials", FieldValue.arrayUnion(material));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addMaterial(@NonNull Material material) {
        try {
            DocumentReference rentDoc = getReference(Material.table, material.getId());
            Task<Void> t = addMaterialAsync(rentDoc);
            Tasks.await(t);
            this.materials.add(rentDoc);
            if (this.materialsMaterialized != null)
                this.getMaterializedUserMaterials().add(material);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //modificata 30/11/2021, non salvo la classe intera RentMaterial ma solo la sua reference sul db
    private Task<Void> removeMaterialAsync(@NonNull DocumentReference material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("materials", FieldValue.arrayRemove(material));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeMaterial(@NonNull Material material) {
        try {
            DocumentReference rentDoc = getReference(Material.table, material.getId());
            Task<Void> t = removeMaterialAsync(rentDoc);
            Tasks.await(t);
            this.materials.remove(rentDoc);
            if (this.materialsMaterialized != null)
                this.getMaterializedUserMaterials().remove(material);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Material> getExpiredMaterial() throws ExecutionException, InterruptedException {
        ArrayList<Material> result = new ArrayList<>();
        Timestamp timestamp = new Timestamp(new Date());

        for (Material material : getMaterializedUserMaterials()) {
            if (timestamp.compareTo(material.getExpiryDate()) >= 0)
                result.add(material);

        }

        return result;
    }

    //aggiunta 17/12/2021
    private Task<Void> addQuarantineAssistanceAsync(@NonNull DocumentReference device) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("quarantineAssistance", FieldValue.arrayUnion(device));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    public boolean addQuarantineAssistance(@NonNull AssistanceType assistanceType, String title,
                                           String description, Date date, double latitude, double longitude) {
        try {
            QuarantineAssistance assistance =
                    createQuarantineAssistance(assistanceType, title, description, date, latitude, longitude);
            DocumentReference quarDoc = getReference(QuarantineAssistance.table, assistance.getId());
            Tasks.await(addQuarantineAssistanceAsync(quarDoc));
            this.quarantineAssistance.add(quarDoc);
            if (this.quarantineAssistanceMaterialized != null)
                this.getMaterializedQuarantineAssistance().add(assistance);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Task<Void> removeQuarantineAssistanceAsync(@NonNull DocumentReference assistance) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("quarantineAssistance", FieldValue.arrayRemove(assistance));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    public boolean removeQuarantineAssistance(@NonNull QuarantineAssistance assistance) {
        try {
            if (getMaterializedDevices().contains(assistance)) {
                DocumentReference quarDoc = getReference(QuarantineAssistance.table, assistance.getId());
                Tasks.await(removeQuarantineAssistanceAsync(quarDoc));
                this.quarantineAssistance.remove(quarDoc);
                if (this.quarantineAssistanceMaterialized != null)
                    this.getMaterializedQuarantineAssistance().remove(assistance);
                quarDoc.delete();
                return true;
            }
            return false;
        } catch (NoDeviceFoundException | NoUserFoundException | ExecutionException | InterruptedException e) {
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

    public Collection<User> activitiesUsers() throws ExecutionException, InterruptedException {
        TreeSet<User> result = new TreeSet<>();
        DocumentReference userDoc = getReference(table, this.id);

        Task<QuerySnapshot> future = getInstance().collection(Activity.table)
                .whereArrayContains("participants", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            Activity activity = getActivityById(doc.getId());

            result.addAll(activity.getMaterializedParticipants());
        }

        result.remove(this);
        return result;
    }

    public List<Activity> GetPositiveActivities() throws ExecutionException, InterruptedException {
        ArrayList<Activity> result = new ArrayList<>();
        DocumentReference userDoc = getReference(table, this.id);

        Task<QuerySnapshot> future = getInstance().collection(Activity.table)
                .whereArrayContains("participants", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            Activity activity = getActivityById(doc.getId());

            for (User u : activity.getMaterializedParticipants()) {
                if (u.getPositiveSince() != null) {
                    result.add(activity);
                    break;
                }
            }
        }

        return result;
    }

    //metodo equal per confronti
    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User user = (User) o;

            return user.getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public int compareTo(User o) {
        if (o.getId().equals(this.getId()))
            return 0;
        return 1;
    }
}
