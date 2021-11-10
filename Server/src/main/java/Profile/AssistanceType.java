package Profile;

import Profile.Exception.NoAssistanceTypeFoundExeption;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.internal.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//TODO all this function are tested
public class AssistanceType {
    private String Type;
    private String ID;

    public AssistanceType(){}

    public AssistanceType(String type, String ID) {
        Type = type;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    private void setID(String ID) {
        this.ID = ID;
    }

    private void setType(String type) {
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public static AssistanceType addAssistanceType(@NonNull String type) throws ExecutionException, InterruptedException {

        try{
            getAssistanceType(type);
        }
        catch (NoAssistanceTypeFoundExeption e) {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> myMap = new HashMap<>();          //create "table"
            myMap.put("type", type);

            ApiFuture<DocumentReference> addedDocRef = db.collection("assistanceType").add(myMap);

            return new AssistanceType(type, addedDocRef.get().getId());
        }
        throw new NoAssistanceTypeFoundExeption("This assistance type( "+type+" ) already exsist ");
    }

    public static AssistanceType getAssistanceType(@NonNull String type) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db

        ApiFuture<QuerySnapshot> future = db.collection("assistanceType").whereEqualTo("type", type).get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if(documents.isEmpty())
            throw new NoAssistanceTypeFoundExeption("No assistance type found with this type: " + type);

        AssistanceType assistance = documents.get(0).toObject(AssistanceType.class);
        assistance.ID = documents.get(0).getId();

        return assistance;
    }

    public static boolean removeAssistanceType(@NonNull String type) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();      //create of object db
        AssistanceType assistanceType = getAssistanceType(type);

        db.collection("assistanceType").document(assistanceType.ID).delete();

        return true;
    }

    public static boolean updateAssistanceType(@NonNull String oldType, @NonNull String newType) throws ExecutionException, InterruptedException {
        if(!oldType.equals(newType)){
            Firestore db = FirestoreClient.getFirestore();      //create of object db

            DocumentReference docRef = db.collection("assistanceType").document(getAssistanceType(oldType).getID());
            ApiFuture<DocumentSnapshot> val = docRef.get();
            DocumentSnapshot document = val.get();

            if(document.exists()){
                docRef.update("type", newType);
                return true;
            }
        }
        return false;
    }

    protected DocumentReference getDocumentReference(){
        Firestore db = FirestoreClient.getFirestore();

        return db.collection("assistanceType").document(this.ID);
    }
}
