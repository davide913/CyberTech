package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Connection.Database.getDocument;
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


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;


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
    private ArrayList<DocumentReference> devices;
    private ArrayList<DocumentReference> lendingInProgresses;
    private ArrayList<DocumentReference> extensionRequests;
    private ArrayList<DocumentReference> rentMaterials;
    private DocumentReference quarantineAssistance;

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
                 Timestamp positiveSince, long lendingPoint, ArrayList<DocumentReference> devices,
                 ArrayList<DocumentReference> lendingInProgresses, ArrayList<DocumentReference> extensionRequest,
                 ArrayList<DocumentReference> rentMaterials, DocumentReference quarantineAssistance) {

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

    public ArrayList<DocumentReference> getExtensionRequests() {
        return extensionRequests;
    }

    private void setExtensionRequests(ArrayList<DocumentReference> extensionRequests) {
        this.extensionRequests = extensionRequests;
    }

    public ArrayList<DocumentReference> getRentMaterials() {
        return rentMaterials;
    }

    private void setRentMaterials(ArrayList<DocumentReference> rentMaterials) {
        this.rentMaterials = rentMaterials;
    }

    public DocumentReference getQuarantineAssistance() {
        return quarantineAssistance;
    }

    private void setQuarantineAssistance(DocumentReference quarantineAssistance) {
        this.quarantineAssistance = quarantineAssistance;
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

    public ArrayList<ExtensionRequest> getMaterializedExtensionRequest() throws ExecutionException, InterruptedException {
        ArrayList<ExtensionRequest> arr = new ArrayList<>();

        for (DocumentReference doc : extensionRequests) {
            arr.add(ExtensionRequest.getExtensionRequestById(doc.getId()));
        }

        return arr;
    }

    public ArrayList<RentMaterial> getMaterializedRentMaterial() throws ExecutionException, InterruptedException {
        ArrayList<RentMaterial> arr = new ArrayList<>();

        for (DocumentReference doc : rentMaterials) {
            arr.add(RentMaterial.getRentMaterialById(doc.getId()));
        }

        return arr;
    }

    public QuarantineAssistance getMaterializedQuarantineAssistance() throws ExecutionException, InterruptedException {
        return QuarantineAssistance.getQuarantineAssistanceById(quarantineAssistance.getId());
    }

    //The setter are private just for don't permit to the library user to change the value. Firebase library needs setters!

    public static User createUser(String id, String name, String surname, Sex sex, String address,
                                  String city, String country, long latitude, long longitude, boolean greenpass) throws ExecutionException, InterruptedException {

        if(sex != Sex.female && sex != Sex.male && sex != Sex.nonBinary)
            throw new NoUserFoundException("for create a user, the sex need to be male, female or nonBinary");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        User user = new User(id, name, surname, sex, address, city, country, new GeoPoint(latitude, longitude),
                greenpass, null, 0, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), null);

        Task<Void> future = db.collection("users").document(id).set(user);
        Tasks.await(future);
        future.getResult();

        return user;
    }

    //TODO verificare che se ci sono degli array a null di inizializzarli! ( in caso cotrario le update dei rispettivi array andrebbero in errore )
    public static User getUserById(String id) throws  InterruptedException, ExecutionException, NoUserFoundException {
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

    private Task<Void> deleteUserAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return deleteFromCollectionAsync("users", id);
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
        DocumentReference docRef = getReference("users", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("greenpass", val);
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
    private Task<Void> updatePositiveSinceAsync(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
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

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> updateLendingPointAsync(long val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
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
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("devices", FieldValue.arrayUnion(device));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addDevice(@NonNull Device device){
        try {
            DocumentReference devDoc = getReference("device", device.getId());
            Task<Void> t = addDeviceAsync(devDoc);
            Tasks.await(t);
            this.devices.add(devDoc);
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
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("devices", FieldValue.arrayRemove(device));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeDevice(@NonNull Device device) {
        try {
            DocumentReference devDoc = getReference("device", device.getId());
            Task<Void> t = removeDeviceAsync(devDoc);
            Tasks.await(t);
            this.devices.remove(devDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //1/12/2021 modificata le update sulla equals ( faccio un confronto sugli id ) e usata la getMaterialized(TYPE) per vedere se gia presente l'oggetto
    public boolean updateDevice(@NonNull Device oldDevice, @NonNull Device newDevice) throws Exception {
        if (!oldDevice.getId().equals(newDevice.getId())) {          //if old and new device are different
            boolean flag = false;
            ArrayList<Device> devices = getMaterializedDevices();

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
    //modificata 30/11/2021, non salvo la classe intera LendingInProgress ma solo la sua reference sul db
    private Task<Void> addLendingAsync(@NonNull DocumentReference lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
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

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    //modificata 30/11/2021, non salvo la classe intera LendingInProgress ma solo la sua reference sul db
    private Task<Void> removeLendingAsync(@NonNull DocumentReference lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
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

    public boolean updateLending(@NonNull LendingInProgress oldLending, @NonNull LendingInProgress newLending) throws Exception {
        if (!oldLending.getId().equals(newLending.getId())) {          //if old and new device are different
            boolean flag = false;
            ArrayList<LendingInProgress> lendingInProgresses = getMaterializedLendingInProgress();

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
    //modificata 30/11/2021, non salvo la classe intera ExtensionRequest ma solo la sua reference sul db
    private Task<Void> addExtensionRequestAsync(@NonNull DocumentReference extensionRequest) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("extensionRequest", FieldValue.arrayUnion(extensionRequest));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addExtensionRequest(@NonNull ExtensionRequest extensionRequest) {
        try {
            DocumentReference extDoc = getReference("extensionRequest", extensionRequest.getId());
            Task<Void> t = addExtensionRequestAsync(extDoc);
            Tasks.await(t);
            this.extensionRequests.add(extDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> removeExtensionRequestAsync(@NonNull DocumentReference extensionRequest) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("extensionRequest", FieldValue.arrayRemove(extensionRequest));
        else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    //modificata 30/11/2021, non salvo la classe intera ExtensionRequest ma solo la sua reference sul db
    public boolean removeExtensionRequest(@NonNull ExtensionRequest extensionRequest) {
        try {
            DocumentReference extDoc = getReference("extensionRequest", extensionRequest.getId());
            Task<Void> t = removeExtensionRequestAsync(extDoc);
            Tasks.await(t);
            this.extensionRequests.remove(extDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExtensionRequest(@NonNull ExtensionRequest oldextensionRequest, @NonNull ExtensionRequest newextensionRequest) throws Exception {
        if (!oldextensionRequest.getId().equals(newextensionRequest.getId())) {
            boolean flag = false;
            ArrayList<ExtensionRequest> extensionRequests = getMaterializedExtensionRequest();

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
    //modificata 30/11/2021, non salvo la classe intera RentMaterial ma solo la sua reference sul db
    private Task<Void> addRentMaterialAsync(@NonNull DocumentReference rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("rentMaterial", FieldValue.arrayUnion(rentMaterial));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean addRentMaterial(@NonNull RentMaterial rentMaterial) {
        try {
            DocumentReference rentDoc = getReference("rentMaterial", rentMaterial.getId());
            Task<Void> t = addRentMaterialAsync(rentDoc);
            Tasks.await(t);
            this.rentMaterials.add(rentDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    //modificata 30/11/2021, non salvo la classe intera RentMaterial ma solo la sua reference sul db
    private Task<Void> removeRentMaterialAsync(@NonNull DocumentReference rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("rentMaterial", FieldValue.arrayRemove(rentMaterial));
        } else
            throw new NoUserFoundException("User not found, id: " + id);
    }

    public boolean removeRentMaterial(@NonNull RentMaterial rentMaterial) {
        try {
            DocumentReference rentDoc = getReference("rentMaterial", rentMaterial.getId());
            Task<Void> t = removeRentMaterialAsync(rentDoc);
            Tasks.await(t);
            this.rentMaterials.remove(rentDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRentMaterial(@NonNull RentMaterial oldrentMaterial, @NonNull RentMaterial newrentMaterial) throws Exception {
        if (!oldrentMaterial.getId().equals(newrentMaterial.getId())) {
            boolean flag = false;
            ArrayList<RentMaterial> rentMaterials = getMaterializedRentMaterial();

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

    //modificata 30/11/2021, non salvo la classe intera quarantine assistance ma solo la sua reference sul db
    private Task<Void> updateQuarantineAsync(QuarantineAssistance quarantineAssistance) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
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
