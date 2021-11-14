package it.unive.cybertech.database.Profile;

import com.google.firebase.firestore.DocumentReference;

public class RentMaterial {
    private DocumentReference idMaterial;

    public RentMaterial(){}

    public RentMaterial(DocumentReference IDmaterial) {
        this.idMaterial = IDmaterial;
    }

    /*public Material getMaterial() throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> val = IDmaterial.get();
        DocumentSnapshot document = val.get();

        return document.toObject(Material.class);
    }*/

    private void setIdMaterial(DocumentReference idMaterial) {
        this.idMaterial = idMaterial;
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
