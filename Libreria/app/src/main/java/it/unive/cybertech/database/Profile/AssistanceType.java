package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.deleteFromCollection;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getInstance;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;

//TODO all this function are tested
public class AssistanceType {
    private String Type;
    private String ID;

    public AssistanceType() {
    }

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

        try {
            getAssistanceType(type);
        } catch (NoAssistanceTypeFoundException e) {
            Map<String, Object> myMap = new HashMap<>();          //create "table"
            myMap.put("type", type);

            DocumentReference addedDocRef = addToCollection("assistanceType", myMap);//db.collection("assistanceType").add(myMap);

            return new AssistanceType(type, addedDocRef.getId());
        }
        throw new NoAssistanceTypeFoundException("This assistance type( " + type + " ) already exsist ");
    }

    public static AssistanceType getAssistanceType(@NonNull String type) throws ExecutionException, InterruptedException {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("assistanceType").whereEqualTo("type", type).get();
        // future.get() blocks on response
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoAssistanceTypeFoundException("No assistance type found with this type: " + type);

        AssistanceType assistance = documents.get(0).toObject(AssistanceType.class);
        assistance.ID = documents.get(0).getId();

        return assistance;
    }

    public static boolean removeAssistanceType(@NonNull String type) throws ExecutionException, InterruptedException {
        AssistanceType assistanceType = getAssistanceType(type);

        deleteFromCollection("assistanceType", assistanceType.ID);//db.collection("assistanceType").document(assistanceType.ID).delete();

        return true;
    }

    public static boolean updateAssistanceType(@NonNull String oldType, @NonNull String newType) throws ExecutionException, InterruptedException {
        if (!oldType.equals(newType)) {
            DocumentReference docRef = getReference("assistanceType", getAssistanceType(oldType).getID());//db.collection("assistanceType").document(getAssistanceType(oldType).getID());
            DocumentSnapshot document = getDocument(docRef);

            if (document.exists()) {
                docRef.update("type", newType);
                return true;
            }
        }
        return false;
    }

    protected DocumentReference getDocumentReference() {
        return getReference("assistanceType", this.ID);//db.collection("assistanceType").document(this.ID);
    }
}
