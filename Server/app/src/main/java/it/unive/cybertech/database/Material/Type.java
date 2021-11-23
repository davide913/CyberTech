package it.unive.cybertech.database.Material;

import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getInstance;
import static it.unive.cybertech.database.Connection.Database.getReference;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Material.Exception.NoTypeFoundException;

public class Type {
    private String typeName;
    private String id;

    public Type(String typeName, String id) {
        this.typeName = typeName;
        this.id = id;
    }

    public String getType() {
        return typeName;
    }

    private void setType(String name) {
        this.typeName = name;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }


    public static Type addType(String name) throws ExecutionException, InterruptedException {
        Type t;
        try{
            t = getTypeByName(name);
        }
        catch (NoTypeFoundException e) {
            Map<String, Object> myType = new HashMap<>();          //create "table"
            myType.put("typeName", name);

            DocumentReference addedDocRef = addToCollection("type", myType);
            t = new Type(name, addedDocRef.getId());
        }

        return t;
    }

    public static Type getTypeById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("type", id);
        DocumentSnapshot document = getDocument(docRef);

        Type type = null;

        if (document.exists()) {
            type = document.toObject(Type.class);
            type.setId(document.getId());

            return type;
        } else
            throw new NoTypeFoundException("No type found with this id: " + id);
    }

    public static Type getTypeByName(String name){
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("assistanceType").whereEqualTo("name", name).get();
        // future.get() blocks on response
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoTypeFoundException("No type found with this name: " + name);

        Type type = documents.get(0).toObject(Type.class);
        type.id = documents.get(0).getId();

        return type;
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> updateTypeNameAsync(String s) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("type", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("typeName", s);
        } else
            throw new NoTypeFoundException("Type not found with this id: " + id);
    }

    public boolean updateTypeName(String s) {
        try {
            Task<Void> t = updateTypeNameAsync(s);
            Tasks.await(t);
            this.typeName = s;
            return true;
        } catch (ExecutionException | InterruptedException | NoTypeFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
