package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.util.*;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundExeption;


//TODO all the function are tested
public class User {
    private String id;
    private String name;
    private String surname;
    private String sex;
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

    private User(String id, String name, String surname, String sex, String address, String city,
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

    private User(String id, String name, String surname, String sex, String address,
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

    public String getSex() {
        return sex;
    }

    private void setSex(String sex) {
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

    public Date getDatePositiveSince() {
        return positiveSince.toDate();
    }           //return a timestamp as a date

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

    //private function for code replication
    protected DocumentReference getDocumentReference() {
        return getReference("users", this.id);
    }

    public static User createUser(String name, String surname, String sex, String address,
                                  String city, String country, GeoPoint position, boolean greenpass) throws Exception {

        //TODO trasformare sesso in un enum
        //TODO non ritornare null ma eccezione
        if (sex.length() > 1 || (!sex.equals("M") && !sex.equals("F")))      //check sex variable
            return null;

        Map<String, Object> myUser = new HashMap<>();          //create "table"
        myUser.put("Name", name);
        myUser.put("Surname", surname);
        myUser.put("Sex", sex);
        myUser.put("Address", address);
        myUser.put("City", city);
        myUser.put("Country", country);
        myUser.put("Geopoint", position);
        myUser.put("Greenpass", greenpass);
        myUser.put("LendingPoint", 0);

        DocumentReference addedDocRef = Database.addToCollection("users", myUser);// db.collection("users").add(myUser);        //push on db

        return new User(addedDocRef.getId(), name, surname, sex, address, city, country, position,
                greenpass, null, 0, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), null);

    }

    public static User getUserById(String id) throws Exception {
        DocumentReference docRef = getReference("user", id);
        DocumentSnapshot document = getDocument(docRef);

        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);
            user.setId(document.getId());

            return user;
        } else
            throw new NoUserFoundExeption("No user found with this id: " + id);

    }

    public boolean deleteUser() {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            Database.deleteFromCollection("users", id);//db.collection("users").document(Id).delete();
        else
            throw new NoUserFoundExeption("No user found with this id: " + id);

        return true;

    }


    public boolean updateGreenPass(boolean val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("Greenpass", val);
            this.greenPass = val;
            return true;
        } else
            return false;
    }


    public boolean updatePositiveSince(Date date) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (date != null) {                    //if the date is null is possible to delete the field date from db
                Timestamp timestamp = new Timestamp(date);            //conversion from date to timestamp
                docRef.update("PositiveSince", timestamp);
                this.positiveSince = timestamp;
            } else {
                docRef.update("PositiveSince", FieldValue.delete());
                this.positiveSince = null;
            }

            return true;
        } else
            return false;
    }


    public boolean updateLendingPoint(long val) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists() && val >= 0) {
            docRef.update("LendingPoint", val);
            this.lendingPoint = val;

            return true;
        } else
            return false;
    }

    public boolean addDevice(@NonNull Device device) throws Exception {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("Devices", FieldValue.arrayUnion(device));
            this.devices.add(device);
            return true;
        } else
            return false;
    }

    public boolean removeDevice(@NonNull Device device) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("Devices", FieldValue.arrayRemove(device));
            this.devices.remove(device);
            return true;
        } else
            return false;
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


    public boolean addLending(@NonNull LendingInProgress lending) throws Exception {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("LendingInProgress", FieldValue.arrayUnion(lending));
            this.lendingInProgresses.add(lending);
            return true;
        } else
            return false;
    }

    public boolean removeLending(@NonNull LendingInProgress lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("LendingInProgress", FieldValue.arrayRemove(lending));
            this.lendingInProgresses.remove(lending);
            return true;
        } else
            return false;
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

    public boolean addExtensionRequest(@NonNull ExtensionRequest extensionRequest) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("ExtensionRequest", FieldValue.arrayUnion(extensionRequest));
            this.extensionRequests.add(extensionRequest);
            return true;
        } else
            return false;
    }


    public boolean removeExtensionRequest(@NonNull ExtensionRequest extensionRequest) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("ExtensionRequest", FieldValue.arrayRemove(extensionRequest));
            this.extensionRequests.remove(extensionRequest);
            return true;
        } else
            return false;
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

    public boolean addRentMaterial(@NonNull RentMaterial rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("RentMaterial", FieldValue.arrayUnion(rentMaterial));
            this.rentMaterials.add(rentMaterial);
            return true;
        } else
            return false;
    }


    public boolean removeRentMaterial(@NonNull RentMaterial rentMaterial) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            docRef.update("RentMaterial", FieldValue.arrayRemove(rentMaterial));
            this.rentMaterials.remove(rentMaterial);
            return true;
        } else
            return false;
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

    public boolean updateQuarantine(QuarantineAssistance quarantineAssistance) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("users", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            if (quarantineAssistance != null) {                  //if the quarantineAssistance is null is possible to delete the field date from db
                docRef.update("quarantineAssistance", quarantineAssistance);
                this.assistance = quarantineAssistance;
            } else {
                docRef.update("quarantineAssistance", FieldValue.delete());
                this.assistance = null;
            }

            return true;
        } else
            return false;
    }

}
