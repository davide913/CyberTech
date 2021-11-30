package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getInstance;
import static it.unive.cybertech.database.Connection.Database.getReference;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;

//TODO testata e funzionante
public class AssistanceType {
    private String Type;
    private String id;

    public AssistanceType() {
    }

    public AssistanceType(String type, String id) {
        Type = type;
        this.id = id;
    }

    public String getID() {
        return id;
    }

    private void setID(String ID) {
        this.id = ID;
    }

    private void setType(String type) {
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public static AssistanceType getAssistanceTypeById(String id) throws  InterruptedException, ExecutionException, NoAssistanceTypeFoundException {
        DocumentReference docRef = getReference("assistanceType", id);
        DocumentSnapshot document = getDocument(docRef);

        AssistanceType assistanceType = null;

        if (document.exists()) {
            assistanceType = document.toObject(AssistanceType.class);
            assistanceType.setID(document.getId());

            return assistanceType;
        } else
            throw new NoAssistanceTypeFoundException("No Assistance Type found with this id: " + id);
    }

    public static ArrayList<AssistanceType> getAssistanceTypes() throws ExecutionException, InterruptedException {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("assistanceType").get();
        // future.get() blocks on response

        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        ArrayList<AssistanceType> arr = new ArrayList<>();
        for (DocumentSnapshot t: documents) {
            AssistanceType assistance = t.toObject(AssistanceType.class);
            assistance.id = t.getId();

            Map<String, Object> map = t.getData();

            arr.add(assistance);
        }

        return arr;
    }

    /*public static AssistanceType createAssistanceType(@NonNull String type) throws ExecutionException, InterruptedException {

        try {
            getAssistanceType(type);
        } catch (NoAssistanceTypeFoundException e) {
            Map<String, Object> myMap = new HashMap<>();          //create "table"
            myMap.put("type", type);

            DocumentReference addedDocRef = addToCollection("assistanceType", myMap);//db.collection("assistanceType").add(myMap);

            return new AssistanceType(type, addedDocRef.getId());
        }
        throw new NoAssistanceTypeFoundException("This assistance type( " + type +
                " ) already exsist with this id: " + getAssistanceType(type).getID());
    }

    public static AssistanceType getAssistanceType(@NonNull String type) throws ExecutionException, InterruptedException {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("assistanceType").whereEqualTo("type", type).get();
        // future.get() blocks on response
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoAssistanceTypeFoundException("No assistance type found with this type: " + type);

        AssistanceType assistance = documents.get(0).toObject(AssistanceType.class);
        assistance.id = documents.get(0).getId();

        return assistance;
    }

    public Task<Void> deleteAssistanceTypeAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("assistanceType", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            return deleteFromCollectionAsync("assistanceType", this.id);
        else
            throw new NoAssistanceTypeFoundException("No assistance type found with this id: " + id);
    }

    public boolean deleteAssistanceType() {
        try {
            Task<Void> t = this.deleteAssistanceTypeAsync();
            Tasks.await(t);
            this.id = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoAssistanceTypeFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Task<Void> updateAssistanceTypeAsync(@NonNull String newType) throws ExecutionException, InterruptedException {
        if (!this.Type.equals(newType)) {
            DocumentReference docRef = getReference("assistanceType", this.id);//db.collection("assistanceType").document(getAssistanceType(oldType).getID());
            DocumentSnapshot document = getDocument(docRef);

            if (document.exists()) {
                return docRef.update("type", newType);
            }
            throw new NoAssistanceTypeFoundException("Update can't work if the type is not saved in the db");
        }
        throw new NoAssistanceTypeFoundException("Update can't work if the new type in the update Assistance type are the same");
    }

    public boolean updateAssistanceType(@NonNull String newType) {
        try {
            Task<Void> t = this.updateAssistanceTypeAsync(newType);
            Tasks.await(t);
            this.Type = newType;
            return true;
        } catch (ExecutionException | InterruptedException | NoAssistanceTypeFoundException e) {
            e.printStackTrace();
            return false;
        }
    }*/
}
