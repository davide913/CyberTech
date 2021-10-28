package Profile;

import Connection.Connection;
import com.google.api.SystemParameterRule;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.type.Date;
import com.google.type.DateTime;
import com.google.type.DateTimeOrBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class User {
    private String Id;
    private String Name;
    private String Surname;
    private String Sex;
    private String Address;
    private String City;
    private String Country;
    private GeoPoint Position;
    private boolean Greenpass;
    private DateTime PositiveSince;
    private long LendingPoint;
    private Collection<Device> Devices;
    private Collection<Profile.LendingInProgress> LendingInProgress;
    private Collection<Profile.ExtensionRequest> ExtensionRequest;
    private Collection<Profile.RentMaterial> RentMaterial;
    private QuarantineAssistance Assistance;

    public User(){}

    private User(String id, String name, String surname, String sex, String address, String city,
                String country, GeoPoint position, boolean greenpass, DateTime positiveSince, long lendingPoint) {
        Id = id;
        Name = name;
        Surname = surname;
        Sex = sex;
        Address = address;
        City = city;
        Country = country;
        Position = position;
        Greenpass = greenpass;
        PositiveSince = positiveSince;
        LendingPoint = lendingPoint;
    }

    private User(String Id, String Name, String cognome, String sex, String address,
                 String city, String country, GeoPoint position, boolean greenpass,
                 DateTime positiveSince, long lendingPoint, Collection<Device> devices,
                 Collection<Profile.LendingInProgress> lendingInProgress, Collection<Profile.ExtensionRequest> extensionRequest,
                 Collection<Profile.RentMaterial> rentMaterial, QuarantineAssistance assistance) {

        this.Id = Id;
        this.Name = Name;
        Surname = cognome;
        Sex = sex;
        Address = address;
        City = city;
        Country = country;
        Position = position;
        Greenpass = greenpass;
        PositiveSince = positiveSince;
        LendingPoint = lendingPoint;
        Devices = devices;
        LendingInProgress = lendingInProgress;
        ExtensionRequest = extensionRequest;
        RentMaterial = rentMaterial;
        Assistance = assistance;
    }

    public String getId(){
        return this.Id;
    }

    private void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    private void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    private void setSurname(String surname) {
        Surname = surname;
    }

    public String getSex() {
        return Sex;
    }

    private void setSex(String sex) {
        Sex = sex;
    }

    public String getAddress() {
        return Address;
    }

    private void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    private void setCity(String city) {
        City = city;
    }

    public String getCountry() {
        return Country;
    }

    private void setCountry(String country) {
        Country = country;
    }

    public GeoPoint getPosition() {
        return Position;
    }

    private void setPosition(GeoPoint position) {
        Position = position;
    }

    public boolean isGreenpass() {
        return Greenpass;
    }

    private void setGreenpass(boolean greenpass) {
        Greenpass = greenpass;
    }

    public DateTime getPositiveSince() {
        return PositiveSince;
    }

    private void setPositiveSince(DateTime positiveSince) {
        PositiveSince = positiveSince;
    }

    public long getLendingPoint() {
        return LendingPoint;
    }

    private void setLendingPoint(long lendingPoint) {
        LendingPoint = lendingPoint;
    }

    public Collection<Device> getDevices() {
        return Devices;
    }

    private void setDevices(Collection<Device> devices) {
        Devices = devices;
    }

    public Collection<Profile.LendingInProgress> getLendingInProgress() {
        return LendingInProgress;
    }

    private void setLendingInProgress(Collection<Profile.LendingInProgress> lendingInProgress) {
        LendingInProgress = lendingInProgress;
    }

    public Collection<Profile.ExtensionRequest> getExtensionRequest() {
        return ExtensionRequest;
    }

    private void setExtensionRequest(Collection<Profile.ExtensionRequest> extensionRequest) {
        ExtensionRequest = extensionRequest;
    }

    public Collection<Profile.RentMaterial> getRentMaterial() {
        return RentMaterial;
    }

    private void setRentMaterial(Collection<Profile.RentMaterial> rentMaterial) {
        RentMaterial = rentMaterial;
    }

    public QuarantineAssistance getAssistance() {
        return Assistance;
    }

    private void setAssistance(QuarantineAssistance assistance) {
        Assistance = assistance;
    }

    //The setter are private just for don't permit to the library user to change the value. Firebase library needs setters!

    public static User createUser(String name, String surname, String sex, String address,
                                  String city, String country, GeoPoint position, boolean greenpass) throws Exception {

        if(sex.length()>1 || (!sex.equals("M") && !sex.equals("F")))      //check sex variable
            return null;

        Firestore db = FirestoreClient.getFirestore();      //create of object db

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

        ApiFuture<DocumentReference> addedDocRef = db.collection("users").add(myUser);        //push on db

        return new User(addedDocRef.get().getId(), name, surname, sex, address, city, country, position,
                                    greenpass, null, 0, new ArrayList<Device>(),
                                    new ArrayList<Profile.LendingInProgress>(), new ArrayList<Profile.ExtensionRequest>(),
                                    new ArrayList<Profile.RentMaterial>(), new QuarantineAssistance());

    }

    public static User getUserById(String id) throws Exception {

        Firestore db = FirestoreClient.getFirestore();      //create of object db

        DocumentReference docRef = db.collection("users").document(id);
        ApiFuture<DocumentSnapshot> var = docRef.get();
        DocumentSnapshot document = var.get();

        //System.out.println(document.getData());

        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);
            user.setId(document.getId());
        }
        else
            throw new NoUserFoundExeption("No user found with this id: "+ id);

        return user;
   }


}
