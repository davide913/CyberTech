package it.unive.cybertech.database.Material;
import static it.unive.cybertech.database.Database.getDocument;
import static it.unive.cybertech.database.Database.getInstance;
import static it.unive.cybertech.database.Database.getReference;

import androidx.annotation.NonNull;

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

import it.unive.cybertech.database.Material.Exception.NoTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;


//TODO testata e funzionante
public class Type {
    public final static String table = "materialType";
    private String typeName;
    private String id;

    public Type(){};

    public Type(String typeName, String id) {
        this.typeName = typeName;
        this.id = id;
    }

    public String getType() {
        return typeName;
    }

    private void setType(String typeName) {
        this.typeName = typeName;
    }

    public String getID() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    //TODO verificare se puo servire o meno
    private static Type getMaterialTypeById(String id) throws  InterruptedException, ExecutionException, NoAssistanceTypeFoundException {
        DocumentReference docRef = getReference(table, id);
        DocumentSnapshot document = getDocument(docRef);

        Type type = null;

        if (document.exists()) {
            type = document.toObject(Type.class);
            type.setId(document.getId());

            return type;
        } else
            throw new NoTypeFoundException("No Material Type found with this id: " + id);
    }

    //TODO funzionante, il toObject non funzionava!
    public static ArrayList<Type> getMaterialTypes() throws ExecutionException, InterruptedException {
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection(table).get();
        // future.get() blocks on response
        Tasks.await(future);
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        ArrayList<Type> arr = new ArrayList<>();
        for (DocumentSnapshot t: documents) {
            Type type = new Type();
            //type = t.toObject(Type.class);
            Map<String, Object> map = t.getData();

            type.setId(t.getId());
            type.setType((String) map.get("typeName"));

            arr.add(type);
        }

        return arr;
    }

    @NonNull
    @Override
    public String toString() {
        return typeName;
    }
}
