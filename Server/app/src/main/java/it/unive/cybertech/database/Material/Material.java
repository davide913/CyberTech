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

//TODO metodo |statico| che non prende nulla e ritorna un arraylist di utenti della coda.

public class Material {
    private String id;
    private DocumentReference proprietario;
    private String titolo;
    private String descrizione;
    private String foto;
    private ArrayList<DocumentReference> coda;
    private DocumentReference idTipologia;

    public Material() {}

    public Material(String id, DocumentReference proprietario, String titolo, String descrizione, String foto,
                    ArrayList<DocumentReference> coda, DocumentReference idTipologia) {
        this.id = id;
        this.proprietario = proprietario;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.foto = foto;
        this.coda = coda;
        this.idTipologia = idTipologia;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public DocumentReference getProprietario() {
        return proprietario;
    }

    private void setProprietario(DocumentReference proprietario) {
        this.proprietario = proprietario;
    }

    public String getTitolo() {
        return titolo;
    }

    private void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    private void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getFoto() {
        return foto;
    }

    private void setFoto(String foto) {
        this.foto = foto;
    }

    public ArrayList<DocumentReference> getCoda() {
        return coda;
    }

    //TODO da testare
    public ArrayList<User> getCodaUsers() throws Exception {
        ArrayList<User> arrayList = new ArrayList<>();

        for (DocumentReference document: coda)
            arrayList.add(User.getUserById(getDocument(document).getId()));


        return arrayList;
    }

    private void setCoda(ArrayList<DocumentReference> coda) {
        this.coda = coda;
    }

    public DocumentReference getIdTipologia() {
        return idTipologia;
    }

    private void setIdTipologia(DocumentReference idTipologia) {
        this.idTipologia = idTipologia;
    }


    //TODO fare i test per la add e la get user coda
    public static Material addMaterial(@NonNull User proprietario, String titolo, String descrizione, String foto,
                                       @NonNull Type idTipologia) throws ExecutionException, InterruptedException {

        DocumentReference docPro = getReference("users", proprietario.getId());
        DocumentReference docType = getReference("type", idTipologia.getId());

        Map<String, Object> myMaterial = new HashMap<>();          //create "table"
        myMaterial.put("Title", titolo);
        myMaterial.put("Owner", docPro);
        myMaterial.put("Description", descrizione);
        myMaterial.put("Photo", foto);
        myMaterial.put("Type", docType);

        DocumentReference addedDocRef = addToCollection("material", myMaterial);

        return new Material(addedDocRef.getId(), docPro, titolo, descrizione, foto, new ArrayList<>(), docType);
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

        if (document.exists())
            throw new NoMaterialFoundException("No material found with this id: " + id);

        Material material = document.toObject(Material.class);
        material.id = document.getId();

        return material;
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> updateTitleAsync(@NonNull String titolo) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Title", titolo);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updateTitle(@NonNull String titolo) {
        try {
            Task<Void> t = updateTitleAsync(titolo);
            Tasks.await(t);
            setTitolo(titolo);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> updateDescriptionAsync(@NonNull String descrizione) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Description", descrizione);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updateDescription(@NonNull String descrizione) {
        try {
            Task<Void> t = updateDescriptionAsync(descrizione);
            Tasks.await(t);
            setDescrizione(descrizione);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> updatePhotoAsync(@NonNull String foto) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Photo", foto);
        } else
            throw new NoMaterialFoundException("material not found, id: " + id);
    }

    public boolean updatePhoto(String foto) {
        try {
            Task<Void> t = updatePhotoAsync(foto);
            Tasks.await(t);
            setFoto(foto);
            return true;
        } catch (ExecutionException | InterruptedException | NoMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> addUserToQueueAsync(@NonNull User user) throws Exception {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists())
            return docRef.update("Queue", FieldValue.arrayUnion(getReference("users", user.getId())));
        else
            throw new NoMaterialFoundException("Material not found, id: " + id);
    }

    public boolean addUserToQueue(@NonNull User user) throws Exception {
        try {
            Task<Void> t = addUserToQueueAsync(user);
            Tasks.await(t);
            this.coda.add(getReference("users", user.getId()));
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     This method invocation doesn't update the state of object, you need to do it manually
     */
    public Task<Void> removeUserToQueueAsync(@NonNull User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", id);
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            return docRef.update("Queue", FieldValue.arrayRemove(getReference("users", user.getId())));
        } else
            throw new NoMaterialFoundException("Material not found, id: " + id);
    }

    public boolean removeUserToQueue(@NonNull User user) {
        try {
            Task<Void> t = removeUserToQueueAsync(user);
            Tasks.await(t);
            this.coda.remove(getReference("users", user.getId()));
            return true;
        } catch (ExecutionException | InterruptedException | NoUserFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}