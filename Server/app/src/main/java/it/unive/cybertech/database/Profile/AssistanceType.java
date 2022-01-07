package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;

public class AssistanceType {
    public final static String table = "assistanceType";
    private String Type;
    private String id;

    public AssistanceType() {
    }

    public AssistanceType(String type, String id) {
        Type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private void setId(String ID) {
        this.id = ID;
    }

    private void setType(String type) {
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public static AssistanceType getAssistanceTypeById(String id) throws  InterruptedException, ExecutionException, NoAssistanceTypeFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        AssistanceType assistanceType = null;

        if (document.exists()) {
            assistanceType = document.toObject(AssistanceType.class);
            assistanceType.setId(document.getId());

            return assistanceType;
        } else
            throw new NoAssistanceTypeFoundException("No Assistance Type found with this id: " + id);
    }

    public static ArrayList<AssistanceType> getAssistanceTypes() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> future = getInstance().collection(table).get();
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        ArrayList<AssistanceType> arr = new ArrayList<>();
        for (DocumentSnapshot t: documents) {
            AssistanceType assistance = t.toObject(AssistanceType.class);
            assistance.id = t.getId();

            arr.add(assistance);
        }

        return arr;
    }

    @Override
    public String toString() {
        return Type;
    }
}
