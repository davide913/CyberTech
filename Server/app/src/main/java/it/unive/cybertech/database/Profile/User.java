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


import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
    private ArrayList<DocumentReference> lendingInProgresses;
    private ArrayList<DocumentReference> materials;
    private ArrayList<DocumentReference> quarantineAssistance;

    //aggiunti 17/12/2021 come cache per le materialized
    private ArrayList<Device> devicesMaterialized;
    private ArrayList<LendingInProgress> lendingInProgressesMaterialized;
    private ArrayList<Material> materialsMaterialized;
    private ArrayList<QuarantineAssistance> quarantineAssistanceMaterialized;

    public User() {
    }

    private User(String id, String name, String surname, Sex sex, Timestamp birthday,
                 String address, String city, String country, GeoPoint location, boolean greenPass,
                 Timestamp positiveSince, long lendingPoint, ArrayList<DocumentReference> devices,
                 ArrayList<DocumentReference> lendingInProgresses, ArrayList<DocumentReference> materials,
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

    public List<DocumentReference> getDevices() {
        return devices;
    }

    private void setDevices(ArrayList<DocumentReference> devices) {
        this.devices = devices;
    }

    public List<DocumentReference> getLendingInProgresses() {
        return lendingInProgresses;
    }

    private void setLendingInProgresses(ArrayList<DocumentReference> lendingInProgresses) {
        this.lendingInProgresses = lendingInProgresses;
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
        if(devicesMaterialized == null) {
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
        if(lendingInProgressesMaterialized == null) {
            lendingInProgressesMaterialized = new ArrayList<>();

            for (DocumentReference doc : lendingInProgresses)
                lendingInProgressesMaterialized.add(LendingInProgress.getLendingInProgressById(doc.getId()));
        }

        return lendingInProgressesMaterialized;
    }

    public List<Material> getMaterializedUserMaterials() throws ExecutionException, InterruptedException {
        if(materialsMaterialized == null) {
            materialsMaterialized = new ArrayList<>();

            for (DocumentReference doc : materials)
                materialsMaterialized.add(Material.getMaterialById(doc.getId()));
        }

        return materialsMaterialized;
    }


    public List<QuarantineAssistance> getMaterializedQuarantineAssistance() throws ExecutionException, InterruptedException {
        if(quarantineAssistanceMaterialized == null) {
            quarantineAssistanceMaterialized = new ArrayList<>();

            for (DocumentReference doc : materials)
                quarantineAssistanceMaterialized.add(QuarantineAssistance.getQuarantineAssistanceById(doc.getId()));
        }

        return quarantineAssistanceMaterialized;
    }

    //modificata il 13/12/2021 -> aggiunta la data di compleanno
    public static User createUser(String id, String name, String surname, Sex sex, Date birthDay,
                                  String address, String city, String country, long latitude, long longitude,
                                  boolean greenpass) throws ExecutionException, InterruptedException {

        if (sex != Sex.female && sex != Sex.male && sex != Sex.nonBinary)
            throw new NoUserFoundException("for create a user, the sex need to be male, female or nonBinary");

        User user = new User(id, name, surname, sex, new Timestamp(birthDay), address, city, country, new GeoPoint(latitude, longitude),
                greenpass, null, 0, new ArrayList<DocumentReference>(),
                new ArrayList<DocumentReference>(),
                new ArrayList<DocumentReference>(), null);

        Task<Void> future = getInstance().collection(table).document(id).set(user);
        Tasks.await(future);

        return user;
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

            if (user.lendingInProgresses == null)
                user.lendingInProgresses = new ArrayList<>();

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
                //this.getMaterializedDevices().add(device);
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
            this.lendingInProgresses.add(lenDoc);
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
            this.lendingInProgresses.remove(lenDoc);
            this.getMaterializedLendingInProgress().remove(lending);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
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
            this.getMaterializedUserMaterials().remove(material);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LendingInProgress> getExpiredLending() throws ExecutionException, InterruptedException {
        ArrayList<LendingInProgress> result = new ArrayList<>();
        Timestamp timestamp = new Timestamp(new Date());

        for (LendingInProgress lending : getMaterializedLendingInProgress()) {
            if(lending.getEndExpiryDate() != null){
                if (timestamp.compareTo(lending.getEndExpiryDate()) < 0)
                    result.add(lending);
            }
            else {
                if (timestamp.compareTo(lending.getExpiryDate()) < 0)
                    result.add(lending);
            }
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
            if(getMaterializedDevices().contains(assistance)){
                DocumentReference quarDoc = getReference(QuarantineAssistance.table, assistance.getId());
                Tasks.await(removeQuarantineAssistanceAsync(quarDoc));
                this.quarantineAssistance.remove(quarDoc);
                this.getMaterializedQuarantineAssistance().remove(assistance);
                quarDoc.delete();
                return true;
            }
            return false;
        } catch ( NoDeviceFoundException | NoUserFoundException | ExecutionException | InterruptedException e) {
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

    public Collection<User> getActivitiesUsers() throws ExecutionException, InterruptedException {
        TreeSet<User> result = new TreeSet<>();
        DocumentReference userDoc = getReference(table, this.id);

        Task<QuerySnapshot> future = getInstance().collection(Activity.table)
                .whereArrayContains("participants", userDoc).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        for (DocumentSnapshot doc : documents ) {
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

        for (DocumentSnapshot doc : documents ) {
            Activity activity = getActivityById(doc.getId());

            for (User u: activity.getMaterializedParticipants() ) {
                if(u.getPositiveSince() != null) {
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
        if(o.getId().equals(this.getId()))
            return 0;
        return 1;
    }
}
