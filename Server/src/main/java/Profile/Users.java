package Profile;

import Profile.Exception.NoUserFoundExeption;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.internal.NonNull;

import java.util.*;
import java.util.concurrent.ExecutionException;

//TODO all the function are tested
public class Users {
    private String Id;
    private String Name;
    private String Surname;
    private String Sex;
    private String Address;
    private String City;
    private String Country;
    private GeoPoint Position;
    private boolean Greenpass;
    private Timestamp PositiveSince;
    private long LendingPoint;
    private ArrayList<Device> Devices;
    private ArrayList<LendingInProgress> LendingInProgress;
    private ArrayList<ExtensionRequest> ExtensionRequest;
    private ArrayList<RentMaterial> RentMaterial;
    private QuarantineAssistance Assistance;

    public Users(){}

    private Users(String id, String name, String surname, String sex, String address, String city,
                  String country, GeoPoint position, boolean greenpass, Timestamp positiveSince, long lendingPoint) {
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

    private Users(String id, String name, String surname, String sex, String address,
                  String city, String country, GeoPoint position, boolean greenpass,
                  Timestamp positiveSince, long lendingPoint, ArrayList<Device> devices,
                  ArrayList<LendingInProgress> lendingInProgress, ArrayList<ExtensionRequest> extensionRequest,
                  ArrayList<RentMaterial> rentMaterial, QuarantineAssistance assistance) {

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

    public Timestamp getPositiveSince() {
        return PositiveSince;
    }

    public Date getDatePositiveSince() {
        return PositiveSince.toDate();
    }           //return a timestamp as a date

    private void setPositiveSince(Timestamp positiveSince) {
        PositiveSince = positiveSince;
    }

    public long getLendingPoint() {
        return LendingPoint;
    }

    private void setLendingPoint(long lendingPoint) {
        LendingPoint = lendingPoint;
    }

    public ArrayList<Device> getDevices() {
        return Devices;
    }

    private void setDevices(ArrayList<Device> devices) {
        Devices = devices;
    }

    public ArrayList<Profile.LendingInProgress> getLendingInProgress() {
        return LendingInProgress;
    }

    private void setLendingInProgress(ArrayList<Profile.LendingInProgress> lendingInProgress) {
        LendingInProgress = lendingInProgress;
    }

    public ArrayList<Profile.ExtensionRequest> getExtensionRequest() {
        return ExtensionRequest;
    }

    private void setExtensionRequest(ArrayList<Profile.ExtensionRequest> extensionRequest) {
        ExtensionRequest = extensionRequest;
    }

    public ArrayList<Profile.RentMaterial> getRentMaterial() {
        return RentMaterial;
    }

    private void setRentMaterial(ArrayList<Profile.RentMaterial> rentMaterial) {
        RentMaterial = rentMaterial;
    }

    public QuarantineAssistance getAssistance() {
        return Assistance;
    }

    private void setAssistance(QuarantineAssistance assistance) {
        Assistance = assistance;
    }

    //The setter are private just for don't permit to the library user to change the value. Firebase library needs setters!

    //private function for code replication
    protected static DocumentReference getReference(String id){
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        return db.collection("users").document(id);
    }

    //private function for code replication
    protected static DocumentSnapshot getDocument(DocumentReference reference) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> val = reference.get();
        return val.get();
    }

    public static Users createUser(String name, String surname, String sex, String address,
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

        return new Users(addedDocRef.get().getId(), name, surname, sex, address, city, country, position,
                                    greenpass, null, 0, new ArrayList<Device>(),
                                    new ArrayList<LendingInProgress>(), new ArrayList<ExtensionRequest>(),
                                    new ArrayList<RentMaterial>(), null);

    }

    public static Users getUserById(String id) throws Exception {
        DocumentReference docRef = getReference(id);
        DocumentSnapshot document = getDocument(docRef);

        Users users = null;

        if (document.exists()) {
            users = document.toObject(Users.class);
            users.setId(document.getId());

            return users;
        }
        else
            throw new NoUserFoundExeption("No user found with this id: "+ id);

    }

    public boolean deleteUser() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db
        DocumentReference docRef = getReference(Id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            db.collection("users").document(Id).delete();
        else
            throw new NoUserFoundExeption("No user found with this id: "+ Id);

        return true;

   }


   public boolean updateGreenPass(@NonNull boolean val) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(this.Id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists()) {
           docRef.update("Greenpass", val);
           this.Greenpass = val;
           return true;
       }
       else
           return false;
   }


   public boolean updatePositiveSince(Date date) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists()) {
           if(date != null){                    //if the date is null is possible to delete the field date from db
               Timestamp timestamp = Timestamp.of(date);            //conversion from date to timestamp
               docRef.update("PositiveSince", timestamp);
               this.PositiveSince = timestamp;
           }
           else {
               docRef.update("PositiveSince", FieldValue.delete());
               this.PositiveSince = null;
           }

           return true;
       }
       else
           return false;
   }


   public boolean updateLendingPoint(@NonNull long val) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists() && val >= 0) {
           docRef.update("LendingPoint", val);
           this.LendingPoint = val;

           return true;
       }
       else
           return false;
   }

   public boolean addDevice( @NonNull Device device) throws Exception {
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("Devices", FieldValue.arrayUnion(device));
           this.Devices.add(device);
           return true;
       }
       else
           return false;
   }

   public boolean removeDevice(@NonNull Device device) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("Devices", FieldValue.arrayRemove(device));
           this.Devices.remove(device);
           return true;
       }
       else
           return false;
   }

   public boolean updateDevice(@NonNull Device oldDevice, @NonNull Device newDevice) throws Exception {
        if(!oldDevice.equals(newDevice)) {          //if old and new device are different
            boolean flag = false;

            for (Device d : Devices) {               //check if the old device is present in the list of that user
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
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("LendingInProgress", FieldValue.arrayUnion(lending));
           this.LendingInProgress.add(lending);
           return true;
       } else
           return false;
   }

   public boolean removeLending(@NonNull LendingInProgress lending) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("LendingInProgress", FieldValue.arrayRemove(lending));
           this.LendingInProgress.remove(lending);
           return true;
       } else
           return false;
   }

   public boolean updateLending(@NonNull LendingInProgress oldLending, @NonNull LendingInProgress newLending) throws Exception {
       if(!oldLending.equals(newLending)) {          //if old and new device are different
           boolean flag = false;

           for (LendingInProgress l : LendingInProgress) {               //check if the old LendingInProgress is present in the list of that user
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
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("ExtensionRequest", FieldValue.arrayUnion(extensionRequest));
           this.ExtensionRequest.add(extensionRequest);
           return true;
       } else
           return false;
   }


   public boolean removeExtensionRequest(@NonNull ExtensionRequest extensionRequest) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("ExtensionRequest", FieldValue.arrayRemove(extensionRequest));
           this.ExtensionRequest.remove(extensionRequest);
           return true;
       } else
           return false;
   }

   public boolean updateExtensionRequest(@NonNull ExtensionRequest oldextensionRequest, @NonNull ExtensionRequest newextensionRequest) throws Exception {
       if(!oldextensionRequest.equals(newextensionRequest)){
           boolean flag = false;

           for (ExtensionRequest l : ExtensionRequest) {               //check if the old ExtensionRequest is present in the list of that user
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
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("RentMaterial", FieldValue.arrayUnion(rentMaterial));
           this.RentMaterial.add(rentMaterial);
           return true;
       } else
           return false;
   }


   public boolean removeRentMaterial(@NonNull RentMaterial rentMaterial) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("RentMaterial", FieldValue.arrayRemove(rentMaterial));
           this.RentMaterial.remove(rentMaterial);
           return true;
       } else
           return false;
   }

   public boolean updateRentMaterial(@NonNull RentMaterial oldrentMaterial, @NonNull RentMaterial newrentMaterial) throws Exception {
       if(!oldrentMaterial.equals(newrentMaterial)){
           boolean flag = false;

           for (RentMaterial l : RentMaterial) {               //check if the old RentMaterial is present in the list of that user
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
       DocumentReference docRef = getReference(Id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists()) {
           if(quarantineAssistance != null) {                  //if the quarantineAssistance is null is possible to delete the field date from db
               docRef.update("quarantineAssistance", quarantineAssistance);
               this.Assistance = quarantineAssistance;
           }
           else {
               docRef.update("quarantineAssistance", FieldValue.delete());
               this.Assistance = null;
           }

           return true;
       }
       else
           return false;
   }

}
