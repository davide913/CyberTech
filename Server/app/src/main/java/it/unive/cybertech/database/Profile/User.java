package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;
import static it.unive.cybertech.database.Groups.Activity.obtainActivityById;
import static it.unive.cybertech.database.Profile.Device.createDevice;
import static it.unive.cybertech.database.Profile.QuarantineAssistance.createQuarantineAssistance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import it.unive.cybertech.database.Geoquerable;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Exception.NoGroupFoundException;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.database.Material.Exception.NoMaterialFoundException;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;

/**
 * Class use to describe a user's instance. it has a field final to describe the table where it is save, it can be use from the other class to access to his table.
 * Every field have a public get and a private set to keep the data as same as database.
 * firebase required a get and set to serialize and deserialize the object; for don't mix our "getter" with the firebase deserialization we call the method obtain
 * The class extend Geoquerable to query the users by their position.
 *
 * @author Davide Finesso
 */
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

    /**
     * Materialize field for increase the performance.
     */
    private ArrayList<Device> devicesMaterialized;
    private ArrayList<LendingInProgress> lendingInProgressMaterialized;
    private ArrayList<Material> materialsMaterialized;
    private ArrayList<QuarantineAssistance> quarantineAssistanceMaterialized;

    /**
     * Public empty constructor use only for firebase database.
     *
     * @author Davide Finesso
     */
    public User() {
    }

    /**
     * Private constructor in order to prevent the programmers to instantiate the class.
     *
     * @author Davide Finesso
     */
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

    @Nullable
    public Date obtainBirthDayToDate() {
        if (birthday != null)
            return birthday.toDate();
        else
            return null;
    }

    private void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    /**
     * The method return the field devices materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<Device> obtainMaterializedDevices() throws ExecutionException, InterruptedException {
        if (devicesMaterialized == null) {
            devicesMaterialized = new ArrayList<>();

            for (DocumentReference doc : devices)
                devicesMaterialized.add(Device.obtainDeviceById(doc.getId()));
        }

        return devicesMaterialized;
    }

    /**
     * The method return the field lending in progress materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    private ArrayList<LendingInProgress> obtainAllMaterializedLendingInProgress() throws ExecutionException, InterruptedException {
        if (lendingInProgressMaterialized == null) {
            lendingInProgressMaterialized = new ArrayList<>();
            for (DocumentReference doc : lendingInProgress)
                lendingInProgressMaterialized.add(LendingInProgress.obtainLendingInProgressById(doc.getId()));
        }
        return lendingInProgressMaterialized;
    }

    /**
     * The method return the field lending in progress materialize with the expiry after now. If is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<LendingInProgress> obtainMaterializedLendingInProgress() throws ExecutionException, InterruptedException {
        obtainAllMaterializedLendingInProgress();
        Timestamp timestamp = Timestamp.now();
        List<LendingInProgress> result = new ArrayList<>();
        for (LendingInProgress lending : lendingInProgressMaterialized) {
            if (timestamp.compareTo(lending.getExpiryDate()) <= 0)
                result.add(lending);
        }
        return result;
    }

    /**
     * The method return the field materials materialize, if is null it create the field and after populate it if and only if the expiry date is after now.
     *
     * @author Davide Finesso
     */
    public List<Material> obtainMaterializedUserMaterials() throws ExecutionException, InterruptedException {
        if (materialsMaterialized == null) {
            materialsMaterialized = new ArrayList<>();
            for (DocumentReference doc : materials)
                try {
                    materialsMaterialized.add(Material.obtainMaterialById(doc.getId()));
                } catch (NoMaterialFoundException e) {
                    e.printStackTrace();
                }
        }
        Timestamp timestamp = Timestamp.now();
        List<Material> result = new ArrayList<>();
        for (Material material : materialsMaterialized) {
            if (timestamp.compareTo(material.getExpiryDate()) <= 0)
                result.add(material);
        }
        return result;
    }

    /**
     * The method return the field quarantine assistance materialize, if is null it create the field and after populate it.
     *
     * @author Davide Finesso
     */
    public List<QuarantineAssistance> obtainMaterializedQuarantineAssistance() throws ExecutionException, InterruptedException {
        if (quarantineAssistanceMaterialized == null) {
            quarantineAssistanceMaterialized = new ArrayList<>();

            for (DocumentReference doc : quarantineAssistance)
                quarantineAssistanceMaterialized.add(QuarantineAssistance.obtainQuarantineAssistanceById(doc.getId()));
        }

        return quarantineAssistanceMaterialized;
    }

    /**
     * The method add to the database a new user and return it.
     *
     * @author Davide Finesso
     */
    public static User createUser(@NonNull String id, @NonNull String name, @NonNull String surname, @NonNull Sex sex, Date birthDay,
                                  @NonNull String address, @NonNull String city, @NonNull String country, long latitude, long longitude,
                                  boolean greenpass) throws ExecutionException, InterruptedException {

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

    /**
     * The method return the user with that id. If there isn't a user with that id it throw an exception.
     *
     * @throws NoUserFoundException if a user with that id doesn't exist
     * @author Davide Finesso
     */
    public static User obtainUserById(@NonNull String id) throws InterruptedException, ExecutionException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            User user = document.toObject(User.class);
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> deleteUserAsync() throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync(table, id);
        else
            throw new NoUserFoundException("No user found with this id: " + id);
    }

    /**
     * The method is use to delete an user from the database and all the reference to him.
     * It start to delete all the quarantine assistance where the user is in charge, after that the method delete the user from all the group and activity where is present, later delete all the  things associate to him and finally the method delete the user
     * It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean deleteUser() {
        try {
            DocumentReference doc = getReference(table, this.id);
            Task<QuerySnapshot> task = getInstance().collection(QuarantineAssistance.table)
                    .whereEqualTo("inCharge", doc).get();
            Tasks.await(task);

            List<DocumentSnapshot> documents = task.getResult().getDocuments();
            for (DocumentSnapshot documentSnapshot : documents)
                QuarantineAssistance.obtainQuarantineAssistanceById(documentSnapshot.getId())
                        .updateInCharge_QuarantineAssistance(null);

            task = getInstance().collection(Group.table)
                    .whereArrayContains("members", doc).get();
            Tasks.await(task);

            documents = task.getResult().getDocuments();
            for (DocumentSnapshot documentSnapshot : documents)
                Group.obtainGroupById(documentSnapshot.getId()).removeMember(this);

            for (Material material : obtainMaterializedUserMaterials())
                material.deleteMaterial();

            for (LendingInProgress lending : obtainAllMaterializedLendingInProgress())
                lending.deleteLendingInProgress();

            for (Device device : obtainMaterializedDevices())
                device.deleteDevice();

            for (QuarantineAssistance assistance : obtainMaterializedQuarantineAssistance())
                assistance.deleteQuarantineAssistance();

            Task<Void> t = deleteUserAsync();
            Tasks.await(t);
            this.setId(null);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateGreenPassAsync(boolean val) throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("greenPass", val);
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    /**
     * The method is use to update an user field greenpass to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updatePositiveSinceAsync(Date date) throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (date != null) {
                Timestamp timestamp = new Timestamp(date);
                return docRef.update("positiveSince", timestamp);
            } else {
                return docRef.update("positiveSince", FieldValue.delete());
            }
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    /**
     * The method is use to update an user field positive since to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateLocationAsync(String newCountry, String newCity, String newAddress, GeoPoint geoPoint)
            throws ExecutionException, InterruptedException, NoUserFoundException {

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

    /**
     * The method is use to update an user field location to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean updateLocation(@NonNull String newCountry, @NonNull String newCity,
                                  @NonNull String newAddress, double latitude, double longitude) {
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> updateLendingPointAsync(long val) throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("lendingPoint", val);
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    /**
     * The method is use to update an user field lending point to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
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

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> addDeviceAsync(@NonNull DocumentReference device)
            throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("devices", FieldValue.arrayUnion(device));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    /**
     * The method is use to add an user device to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean addDevice(@NonNull String token, @NonNull String deviceId) {
        try {
            Device device = createDevice(token, deviceId, this.id);
            DocumentReference devDoc = getReference(Device.table, device.getId());

            Tasks.await(addDeviceAsync(devDoc));
            this.devices.add(devDoc);
            if (this.devicesMaterialized != null)
                this.obtainMaterializedDevices().add(device);

            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> removeDeviceAsync(@NonNull DocumentReference device)
            throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("devices", FieldValue.arrayRemove(device));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    /**
     * The method is use to remove an user device to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean removeDevice(@NonNull Device device) {
        try {
            DocumentReference devDoc = getReference(Device.table, device.getId());
            Tasks.await(removeDeviceAsync(devDoc));
            this.devices.remove(devDoc);
            if (this.devicesMaterialized != null)
                this.obtainMaterializedDevices().remove(device);

            device.deleteDevice();
            return true;
        } catch (NoDeviceFoundException | NoUserFoundException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> addLendingAsync(@NonNull DocumentReference lending)
            throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("lendingInProgress", FieldValue.arrayUnion(lending));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    /**
     * The method is use to add an user lending to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean addLending(@NonNull LendingInProgress lending) {
        try {
            DocumentReference lenDoc = getReference(LendingInProgress.table, lending.getId());
            Task<Void> t = addLendingAsync(lenDoc);
            Tasks.await(t);
            this.lendingInProgress.add(lenDoc);
            if (this.lendingInProgressMaterialized != null)
                this.obtainAllMaterializedLendingInProgress().add(lending);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> removeLendingAsync(@NonNull DocumentReference lending)
            throws ExecutionException, InterruptedException, NoUserFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("lendingInProgress", FieldValue.arrayRemove(lending));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    /**
     * The method is use to remove an user lending to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean removeLending(@NonNull LendingInProgress lending) {
        try {
            DocumentReference lenDoc = getReference(LendingInProgress.table, lending.getId());
            Task<Void> t = removeLendingAsync(lenDoc);
            Tasks.await(t);
            this.lendingInProgress.remove(lenDoc);
            if (this.lendingInProgressMaterialized != null)
                this.obtainAllMaterializedLendingInProgress().remove(lending);

            lending.deleteLendingInProgress();
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The method is use to obtain all the expired user's lending in progress from the database.
     *
     * @author Davide Finesso
     */
    public List<LendingInProgress> obtainExpiredLending() throws ExecutionException, InterruptedException {
        ArrayList<LendingInProgress> result = new ArrayList<>();
        Timestamp timestamp = Timestamp.now();

        for (LendingInProgress lending : obtainAllMaterializedLendingInProgress()) {
            if (timestamp.compareTo(lending.getExpiryDate()) > 0 && !lending.getWaitingForFeedback())
                result.add(lending);
        }

        return result;
    }

    /**
     * The method is use to obtain all the expired lending with the user materials from the database.
     *
     * @author Davide Finesso
     */
    public List<LendingInProgress> obtainMyMaterialsExpiredLending() throws ExecutionException, InterruptedException {
        ArrayList<LendingInProgress> result = new ArrayList<>();

        if (!materials.isEmpty()) {
            Task<QuerySnapshot> future = getInstance().collection(LendingInProgress.table)
                    .whereIn("material", materials).whereLessThan("expiryDate", Timestamp.now()).get();
            Tasks.await(future);

            for (DocumentSnapshot ref : future.getResult().getDocuments())
                result.add(LendingInProgress.obtainLendingInProgressById(ref.getReference().getId()));
        }

        return result;
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> addMaterialAsync(@NonNull DocumentReference material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("materials", FieldValue.arrayUnion(material));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    /**
     * The method is use to add an user material to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean addMaterial(@NonNull Material material) {
        try {
            DocumentReference rentDoc = getReference(Material.table, material.getId());
            Task<Void> t = addMaterialAsync(rentDoc);
            Tasks.await(t);
            this.materials.add(rentDoc);
            if (this.materialsMaterialized != null)
                this.obtainMaterializedUserMaterials().add(material);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> removeMaterialAsync(@NonNull DocumentReference material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("materials", FieldValue.arrayRemove(material));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    /**
     * The method is use to remove an user material to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean removeMaterial(@NonNull Material material) {
        try {
            DocumentReference rentDoc = getReference(Material.table, material.getId());
            Task<Void> t = removeMaterialAsync(rentDoc);
            Tasks.await(t);
            this.materials.remove(rentDoc);
            if (this.materialsMaterialized != null)
                this.obtainMaterializedUserMaterials().remove(material);

            material.deleteMaterial();
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The method is use to obtain all the user's expired material from the database.
     *
     * @author Davide Finesso
     */
    public List<Material> getExpiredMaterial() throws ExecutionException, InterruptedException {
        ArrayList<Material> result = new ArrayList<>();
        Timestamp timestamp = Timestamp.now();

        for (Material material : obtainMaterializedUserMaterials()) {
            if (timestamp.compareTo(material.getExpiryDate()) >= 0)
                result.add(material);

        }

        return result;
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> addQuarantineAssistanceAsync(@NonNull DocumentReference device) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("quarantineAssistance", FieldValue.arrayUnion(device));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    /**
     * The method is use to add an user quarantine assistance to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean addQuarantineAssistance(@NonNull AssistanceType assistanceType, @NonNull String title,
                                           @NonNull String description, @NonNull Date date, double latitude, double longitude) {
        try {
            QuarantineAssistance assistance =
                    createQuarantineAssistance(assistanceType, title, description, date, latitude, longitude);
            DocumentReference quarDoc = getReference(QuarantineAssistance.table, assistance.getId());
            Tasks.await(addQuarantineAssistanceAsync(quarDoc));
            this.quarantineAssistance.add(quarDoc);
            if (this.quarantineAssistanceMaterialized != null)
                this.obtainMaterializedQuarantineAssistance().add(assistance);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * the private method is use to update the changes in the database. it returns a task and the caller function waits until it finishes.
     *
     * @author Davide Finesso
     */
    private Task<Void> removeQuarantineAssistanceAsync(@NonNull DocumentReference assistance) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("quarantineAssistance", FieldValue.arrayRemove(assistance));
        else
            throw new NoUserFoundException("User not found with this id: " + id);
    }

    /**
     * The method is use to remove an user quarantine assistance to the database. It return a boolean value that describe if the operation was done.
     *
     * @author Davide Finesso
     */
    public boolean removeQuarantineAssistance(@NonNull QuarantineAssistance assistance) {
        try {
            List<QuarantineAssistance> tmp = obtainMaterializedQuarantineAssistance()
                    .stream().filter(q -> q.getId().equals(assistance.getId())).collect(Collectors.toList());
            if (tmp.size() > 0) {
                DocumentReference quarDoc = getReference(QuarantineAssistance.table, assistance.getId());
                Tasks.await(removeQuarantineAssistanceAsync(quarDoc));
                this.quarantineAssistance.remove(quarDoc);
                if (this.quarantineAssistanceMaterialized != null)
                    this.obtainMaterializedQuarantineAssistance().remove(tmp.get(0));

                assistance.deleteQuarantineAssistance();
                return true;
            }
            return false;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The method is use to delete all the quarantine assistance from a user and database as well.
     *
     * @author Davide Finesso
     */
    public void deleteAllMyQuarantineAssistance() throws ExecutionException, InterruptedException {
        for (QuarantineAssistance quarantineAssistance : obtainMaterializedQuarantineAssistance())
            this.removeQuarantineAssistance(quarantineAssistance);
    }

    /**
     * The method is use to obtain all the lending in progress from a user describe by a passed id.
     *
     * @author Davide Finesso
     */
    public static List<LendingInProgress> obtainUserLendingInProgress(@NonNull String id) throws ExecutionException, InterruptedException {
        return obtainUserById(id).obtainMaterializedLendingInProgress();
    }

    /**
     * The method is use to obtain all the devices from a user describe by a passed id.
     *
     * @author Davide Finesso
     */
    public static List<Device> obtainUserDevices(@NonNull String id) throws ExecutionException, InterruptedException {
        return obtainUserById(id).obtainMaterializedDevices();
    }

    /**
     * The method is use to obtain a collection with all users ( not repeated ) that are in the same activities of caller one.
     *
     * @author Davide Finesso
     */
    public Collection<User> obtainActivitiesUsers() throws ExecutionException, InterruptedException {
        TreeSet<User> result = new TreeSet<>();
        DocumentReference userDoc = getReference(table, this.id);

        Task<QuerySnapshot> future = getInstance().collection(Activity.table)
                .whereArrayContains("participants", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            Activity activity = obtainActivityById(doc.getId());

            result.addAll(activity.obtainMaterializedParticipants());
        }

        result.remove(this);
        return result;
    }

    /**
     * The method is use to obtain a list with all activities that have at least one positive as member.
     *
     * @author Davide Finesso
     */
    public List<Activity> obtainPositiveActivities() throws ExecutionException, InterruptedException {
        ArrayList<Activity> result = new ArrayList<>();
        DocumentReference userDoc = getReference(table, this.id);

        Task<QuerySnapshot> future = getInstance().collection(Activity.table)
                .whereArrayContains("participants", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            Activity activity = obtainActivityById(doc.getId());

            for (User u : activity.obtainMaterializedParticipants()) {
                if (u.getPositiveSince() != null && !u.equals(this)) {
                    result.add(activity);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Obtain the groups where the user is in
     *
     * @author Davide Finesso
     */
    public List<Group> obtainGroups() throws ExecutionException, InterruptedException {
        List<Group> result = new ArrayList<>();
        DocumentReference userDoc = getReference(table, this.id);

        Task<QuerySnapshot> future = getInstance().collection(Group.table)
                .whereArrayContains("members", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents) {
            try {
                Group g = Group.obtainGroupById(doc.getId());
                result.add(g);
            } catch (NoGroupFoundException e) {
            }
        }
        return result;
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
        User user = (User) o;
        return Objects.equals(id, user.id);
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

    /**
     * Used for don't have the same user in a set. it return 0 if the id are the same, 1 otherwise.
     *
     * @author Davide Finesso
     */
    @Override
    public int compareTo(User o) {
        if (o.getId().equals(this.getId()))
            return 0;
        return 1;
    }
}