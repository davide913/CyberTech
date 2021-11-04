package Profile;

import Profile.Exception.NoUserFoundExeption;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.internal.NonNull;
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
    private Timestamp PositiveSince;
    private long LendingPoint;
    private ArrayList<Device> Devices;
    private ArrayList<LendingInProgress> LendingInProgress;
    private ArrayList<ExtensionRequest> ExtensionRequest;
    private ArrayList<RentMaterial> RentMaterial;
    private QuarantineAssistance Assistance;

    public User(){}

    private User(String id, String name, String surname, String sex, String address, String city,
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

    private User(String Id, String Name, String cognome, String sex, String address,
                 String city, String country, GeoPoint position, boolean greenpass,
                 Timestamp positiveSince, long lendingPoint, ArrayList<Device> devices,
                 ArrayList<LendingInProgress> lendingInProgress, ArrayList<ExtensionRequest> extensionRequest,
                 ArrayList<RentMaterial> rentMaterial, QuarantineAssistance assistance) {

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

    //TODO funzione private per ripetizione di codice
    private static DocumentReference getReference(String id){
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        return db.collection("users").document(id);
    }

    //TODO funzione private per ripetizione di codice
    private static DocumentSnapshot getDocument(DocumentReference reference) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> val = reference.get();
        return val.get();
    }

    //tested
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
                                    new ArrayList<LendingInProgress>(), new ArrayList<ExtensionRequest>(),
                                    new ArrayList<RentMaterial>(), new QuarantineAssistance());

    }

    //tested
    public static User getUserById(String id) throws Exception {
        DocumentReference docRef = getReference(id);
        DocumentSnapshot document = getDocument(docRef);

        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);
            user.setId(document.getId());

            return user;
        }
        else
            throw new NoUserFoundExeption("No user found with this id: "+ id);

    }

    //tested
    public static boolean deleteUser(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db
        DocumentReference docRef = getReference(id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            db.collection("users").document(id).delete();
        else
            throw new NoUserFoundExeption("No user found with this id: "+ id);

        return true;

   }

    //tested
   public static boolean updateGreenPass(@NonNull String id,@NonNull boolean val) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists()) {
           docRef.update("Greenpass", val);
           return true;
       }
       else
           return false;
   }

    //tested
   public static boolean updatePositiveSince(@NonNull String id, Date date) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists()) {
           if(date != null){                    //if the date is null is possible to delete the field date from db
               Timestamp timestamp = Timestamp.of(date);            //conversion from date to timestamp
               docRef.update("PositiveSince", timestamp);
           }
           else
               docRef.update("PositiveSince", FieldValue.delete());

           return true;
       }
       else
           return false;
   }

    //tested
   public static boolean updateLendingPoint(@NonNull String id,@NonNull long val) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists() && val >= 0) {
           docRef.update("LendingPoint", val);

           return true;
       }
       else
           return false;
   }

   //tested
   public static boolean addDevice(@NotNull String id, @NonNull Device device) throws Exception {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("Devices", FieldValue.arrayUnion(device));
           return true;
       }
       else
           return false;
   }

   //tested
   public static boolean removeDevice(@NotNull String id, @NonNull Device device) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("Devices", FieldValue.arrayRemove(device));
           return true;
       }
       else
           return false;
   }

   public static boolean updateDevice(@NotNull String id, @NonNull Device oldDevice, @NonNull Device newDevice) throws Exception {
        if(!oldDevice.equals(newDevice)) {          //if old and new device are different
            User myuser = getUserById(id);          //it also manage the wrong id
            boolean flag = false;

            for (Device d : myuser.Devices) {               //check if the old device is present in the list of that user
                if (d.equals(oldDevice)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {             //if find the old device it can update that
                removeDevice(id, oldDevice);
                addDevice(id, newDevice);
                return true;
            }
        }
        return false;
   }

    //tested
   public static boolean addLending(@NotNull String id, @NonNull LendingInProgress lending) throws Exception {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("LendingInProgress", FieldValue.arrayUnion(lending));
           return true;
       } else
           return false;
   }

   //tested
   public static boolean removeLending(@NotNull String id, @NonNull LendingInProgress lending) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("LendingInProgress", FieldValue.arrayRemove(lending));
           return true;
       } else
           return false;
   }

   //tested
   public static boolean updateLending(@NotNull String id, @NonNull LendingInProgress oldLending, @NonNull LendingInProgress newLending) throws Exception {
       if(!oldLending.equals(newLending)) {          //if old and new device are different
           User myuser = getUserById(id);          //it also manage the wrong id
           boolean flag = false;

           for (LendingInProgress l : myuser.LendingInProgress) {               //check if the old device is present in the list of that user
               if (l.equals(oldLending)) {
                   flag = true;
                   break;
               }
           }

           if (flag) {             //if find the old device it can update that
               removeLending(id, oldLending);
               addLending(id, newLending);
               return true;
           }
       }
       return false;
   }

    /*TODO da testate tutti gli add e remove alle collection sottostanti, aggiungere gli update.
       DOMANDA: eliminare la collection se é vuota o tenerla vuota nel dbv una volta creata?!
     */
   public static boolean addExtensionRequest(@NotNull String id, @NonNull ExtensionRequest extensionRequest) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("ExtensionRequest", FieldValue.arrayUnion(extensionRequest));
           return true;
       } else
           return false;
   }

   public static boolean removeExtensionRequest(@NotNull String id, @NonNull ExtensionRequest extensionRequest) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("ExtensionRequest", FieldValue.arrayRemove(extensionRequest));
           return true;
       } else
           return false;
   }

   public static boolean addRentMaterial(@NotNull String id, @NonNull RentMaterial rentMaterial) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("RentMaterial", FieldValue.arrayUnion(rentMaterial));
           return true;
       } else
           return false;
   }

   public static boolean removeRentMaterial(@NotNull String id, @NonNull RentMaterial rentMaterial) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if (document.exists()) {
           docRef.update("RentMaterial", FieldValue.arrayRemove(rentMaterial));
           return true;
       } else
           return false;
   }

   //tested
   public static boolean updateQuarantine(@NotNull String id, QuarantineAssistance quarantineAssistance) throws ExecutionException, InterruptedException {
       DocumentReference docRef = getReference(id);
       DocumentSnapshot document = getDocument(docRef);

       if(document.exists()) {
           if(quarantineAssistance != null)                  //if the quarantineAssistance is null is possible to delete the field date from db
               docRef.update("quarantineAssistance", quarantineAssistance);
           else
               docRef.update("quarantineAssistance", FieldValue.delete());

           return true;
       }
       else
           return false;
   }


}
