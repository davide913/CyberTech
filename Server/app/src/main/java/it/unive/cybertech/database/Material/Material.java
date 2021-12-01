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
    private ArrayList<DocumentReference> queue;
    private DocumentReference type;

    public Material() {}

    public Material(String id, DocumentReference owner, String title, String decription, String photo,
                    ArrayList<DocumentReference> queue, DocumentReference type) {
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

    public ArrayList<DocumentReference> getQueue() {
        return queue;
    }

    private void setQueue(ArrayList<DocumentReference> queue) {
        this.queue = queue;
    }

    public DocumentReference getType() {
        return type;
    }

    private void setType(DocumentReference type) {
        this.type = type;
    }

    public ArrayList<User> getMaterializedQueue() throws ExecutionException, InterruptedException {
        ArrayList<User> arr = new ArrayList<>();

        for (DocumentReference doc : queue) {
            arr.add(User.getUserById(doc.getId()));
        }

        return arr;
    }


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
    private Task<Void> addUserToQueueAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("Queue", FieldValue.arrayUnion(user));
        else
            throw new NoMaterialFoundException("Material not found, id: " + id);
    }

    public boolean addUserToQueue(@NonNull User user){
        try {
            DocumentReference userDoc = getReference("users", user.getId());
            Task<Void> t = addUserToQueueAsync(userDoc);
            Tasks.await(t);
            this.queue.add(userDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    private Task<Void> removeUserToQueueAsync(@NonNull DocumentReference user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Queue", FieldValue.arrayRemove(user));
        } else
            throw new NoMaterialFoundException("Material not found, id: " + id);
    }

    public boolean removeUserToQueue(@NonNull User user) {
        try {
            DocumentReference userDoc = getReference("users", user.getId());
            Task<Void> t = removeUserToQueueAsync(userDoc);
            Tasks.await(t);
            this.queue.remove(userDoc);
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}