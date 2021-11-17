package it.unive.cybertech.database.Profile;

import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.deleteFromCollectionAsync;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Connection.Database;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoAssistanceTypeFoundException;
import it.unive.cybertech.database.Profile.Exception.NoDeviceFoundException;
import it.unive.cybertech.database.Profile.Exception.NoRentMaterialFoundException;

public class RentMaterial {
    private DocumentReference idMaterial;
    private String id;

    public RentMaterial(){}

    public RentMaterial(DocumentReference idMaterial, String id) {
        this.idMaterial = idMaterial;
        this.id = id;
    }

    public DocumentReference getIdMaterial() {
        return idMaterial;
    }

    private void setIdMaterial(DocumentReference idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }



    public RentMaterial addRentMaterial(Material material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", material.getId());
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            Map<String, Object> myRent = new HashMap<>();
            myRent.put("reference", docRef);

            DocumentReference addedDocRef = Database.addToCollection("rentMaterial", myRent);

            return new RentMaterial(docRef, addedDocRef.getId());
        } else
            throw new NoRentMaterialFoundException("No material found with this id: " + material.getId());
    }

    public void removeRentMaterial(){

    }
    public Task<Void> deleteRentMaterialAsync() throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("rentMaterial", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists())
            return deleteFromCollectionAsync("rentMaterial", this.id);
        else
            throw new NoRentMaterialFoundException("No rent material found with this id: " + id);
    }

    public boolean deleteRentMaterial() {
        try {
            Task<Void> t = this.deleteRentMaterialAsync();
            Tasks.await(t);
            this.id = null;
            this.idMaterial = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoRentMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Task<Void> updateRentMaterialAsync(@NonNull Material material) throws ExecutionException, InterruptedException {
        DocumentReference matDoc = getReference("material", material.getId());

        //check if they are the same material
        if (!this.idMaterial.getId().equals(matDoc.getId()) ) {
            DocumentReference docRef = getReference("material", this.id);
            DocumentSnapshot document = getDocument(docRef);

            if (document.exists()) {
                return docRef.update("reference", matDoc);
            }
            throw new NoRentMaterialFoundException("Update of rent material can't work if the material with this id:" +material.getId()+" is not save in db");
        }
        throw new NoRentMaterialFoundException("You are tring to update a rentMaterial with the same object with id: " + material.getId());
    }

    public boolean updateRentMaterial(@NonNull Material material) {
        try {
            Task<Void> t = this.updateRentMaterialAsync(material);
            Tasks.await(t);
            this.idMaterial = getReference("material", material.getId());
            return true;
        } catch (ExecutionException | InterruptedException | NoRentMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }






    @Override
    public boolean equals(Object o){
        if(o instanceof RentMaterial){
            RentMaterial r = (RentMaterial) o;
            return r.idMaterial.equals(this.idMaterial);
        }
        return false;
    }
}
