package it.unive.cybertech.database.Material;

import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Material.Exception.NoMaterialFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;
import it.unive.cybertech.database.Profile.User;

//TODO testata e funzionante!

public class Material {
    private String id;
    private DocumentReference owner;
    private String title;
    private String decription;
    private String photo;
    private ArrayList<User> queue;
    private DocumentReference type;

    public Material() {}

    public Material(String id, DocumentReference owner, String title, String decription, String photo,
                    ArrayList<User> queue, DocumentReference type) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.decription = decription;
        this.photo = photo;
        this.queue = queue;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    private void setOwner(DocumentReference owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return decription;
    }

    private void setDescription(String decription) {
        this.decription = decription;
    }

    public String getPhoto() {
        return photo;
    }

    private void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<User> getQueue() {
        return queue;
    }

    private void setQueue(ArrayList<User> queue) {
        this.queue = queue;
    }

    public DocumentReference getType() {
        return type;
    }

    private void setType(DocumentReference type) {
        this.type = type;
    }


    //TODO fare i test per la add e la get user coda
    public static Material createMaterial(@NonNull User owner, String title, String description, String photo,
                                       @NonNull Type type) throws ExecutionException, InterruptedException {

        DocumentReference docPro = getReference("users", owner.getId());
        DocumentReference docType = getReference("type", type.getID());

        Map<String, Object> myMaterial = new HashMap<>();          //create "table"
        myMaterial.put("title", title);
        myMaterial.put("owner", docPro);
        myMaterial.put("description", description);
        myMaterial.put("photo", photo);
        myMaterial.put("type", docType);

        DocumentReference addedDocRef = addToCollection("material", myMaterial);

        return new Material(addedDocRef.getId(), docPro, title, description, photo, new ArrayList<>(), docType);
    }

    //TODO vedere se ha senso fatre un getbyname perche potrebbero esserci piu materiali con lo stesso nome
    /*public static Material getMaterialByName(String name){
        FirebaseFirestore db = getInstance();      //create of object db

        Task<QuerySnapshot> future = db.collection("material").whereEqualTo("name", name).get();
        // future.get() blocks on response
        List<DocumentSnapshot> documents = future.getResult().getDocuments();

        if (documents.isEmpty())
            throw new NoMaterialFoundException("No material found with this name: " + name);

        Material material = documents.get(0).toObject(Material.class);
        material.id = documents.get(0).getId();

        return material;
    }*/

    //TODO verificare la get per la parte degli user ( salvati come document reference ma usati come ipo user )
    public static Material getMaterialById(@NonNull String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        Material material = null;

        if (document.exists()){
            material = document.toObject(Material.class);
            material.id = document.getId();

            return material;
        }

        throw new NoMaterialFoundException("No material found with this id: " + id);
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> updateTitleAsync(@NonNull String title) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Title", title);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updateTitle(@NonNull String title) {
        try {
            Task<Void> t = updateTitleAsync(title);
            Tasks.await(t);
            setTitle(title);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> updateDescriptionAsync(@NonNull String description) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Description", description);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updateDescription(@NonNull String description) {
        try {
            Task<Void> t = updateDescriptionAsync(description);
            Tasks.await(t);
            setDescription(description);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> updatePhotoAsync(@NonNull String photo) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Photo", photo);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updatePhoto(String photo) {
        try {
            Task<Void> t = updatePhotoAsync(photo);
            Tasks.await(t);
            setPhoto(photo);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //TODO cercare prima che l'utente sia presente nell'array per rimuoverlo o per agiungerlo
    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> addUserToQueueAsync(@NonNull User user) throws Exception {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("Queue", FieldValue.arrayUnion(getReference("users", user.getId())));
        else
            throw new NoMaterialFoundException("Material not found, id: " + id);
    }

    public boolean addUserToQueue(@NonNull User user) throws Exception {
        try {
            if(this.queue.contains(user)) //|| owner.getId().equals(user.getId()))
                return false;
            Task<Void> t = addUserToQueueAsync(user);
            Tasks.await(t);
            this.queue.add(user);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> removeUserToQueueAsync(@NonNull User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Queue", FieldValue.arrayRemove(getReference("users", user.getId())));
        } else
            throw new NoMaterialFoundException("Material not found, id: " + id);
    }

    public boolean removeUserToQueue(@NonNull User user) {
        try {
            if(!queue.contains(user))
                return false;
            Task<Void> t = removeUserToQueueAsync(user);
            Tasks.await(t);
            this.queue.remove(user);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}