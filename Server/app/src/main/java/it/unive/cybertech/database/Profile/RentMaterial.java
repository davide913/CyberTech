package it.unive.cybertech.database.Profile;

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
import it.unive.cybertech.database.Profile.Exception.NoRentMaterialFoundException;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;

public class RentMaterial {
    private DocumentReference referenceMaterial;
    private String id;

    public RentMaterial(){}

    public RentMaterial(DocumentReference referenceMaterial, String id) {
        this.referenceMaterial = referenceMaterial;
        this.id = id;
    }

    public DocumentReference getReferenceMaterial() {
        return referenceMaterial;
    }

    public Material getMaterial() throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getDocument(referenceMaterial);

        if(document.exists())
            return Material.getMaterialById(document.getId());

        return null;
    }

    private void setReferenceMaterial(DocumentReference referenceMaterial) {
        this.referenceMaterial = referenceMaterial;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }


    public static RentMaterial getRentMaterialById(String id) throws  InterruptedException, ExecutionException, NoUserFoundException {
        DocumentReference docRef = getReference("rentMaterial", id);
        DocumentSnapshot document = getDocument(docRef);

        RentMaterial rentMaterial = null;

        if (document.exists()) {
            rentMaterial = document.toObject(RentMaterial.class);
            rentMaterial.setId(document.getId());

            return rentMaterial;
        } else
            throw new NoRentMaterialFoundException("No rent material found with this id: " + id);
    }

    public static RentMaterial createRentMaterial(Material material) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("material", material.getId());
        DocumentSnapshot document = getDocument(docRef);

        if (document.exists()) {
            Map<String, Object> myRent = new HashMap<>();
            myRent.put("referenceMaterial", docRef);

            DocumentReference addedDocRef = Database.addToCollection("rentMaterial", myRent);

            return new RentMaterial(docRef, addedDocRef.getId());
        } else
            throw new NoRentMaterialFoundException("No material found with this id: " + material.getId());
    }

    private Task<Void> deleteRentMaterialAsync() throws ExecutionException, InterruptedException {
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
            this.referenceMaterial = null;
            return true;
        } catch (ExecutionException | InterruptedException | NoRentMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task<Void> updateRentMaterialAsync(@NonNull Material material) throws ExecutionException, InterruptedException {
        DocumentReference matDoc = getReference("material", material.getId());

        //check if they are the same material
        if (!this.referenceMaterial.getId().equals(matDoc.getId()) ) {
            DocumentReference docRef = getReference("rentMaterial", this.id);
            DocumentSnapshot document = getDocument(docRef);

            if (document.exists()) {
                return docRef.update("referenceMaterial", matDoc);
            }
            throw new NoRentMaterialFoundException("Update of rent material can't work if the material with this id:" +
                    material.getId()+" is not save in db");
        }
        throw new NoRentMaterialFoundException("You are tring to update a rentMaterial with the same object with id: " + material.getId());
    }

    public boolean updateRentMaterial(@NonNull Material material) {
        try {
            Task<Void> t = this.updateRentMaterialAsync(material);
            Tasks.await(t);
            this.referenceMaterial = getReference("material", material.getId());
            return true;
        } catch (ExecutionException | InterruptedException | NoRentMaterialFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*@Override
    public boolean equals(Object o){
        if(o instanceof RentMaterial){
            RentMaterial r = (RentMaterial) o;
            return r.referenceMaterial.equals(this.referenceMaterial);
        }
        return false;
    }*/
}
