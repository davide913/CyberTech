package it.unive.cybertech.database.Profile;

import com.google.firebase.firestore.DocumentReference;

public class RentMaterial {
    private DocumentReference IDmaterial;

    public RentMaterial(){}

    public RentMaterial(DocumentReference IDmaterial) {
        this.IDmaterial = IDmaterial;
    }

    /*public Material getMaterial() throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> val = IDmaterial.get();
        DocumentSnapshot document = val.get();

        return document.toObject(Material.class);
    }*/

    private void setIDmaterial(DocumentReference IDmaterial) {
        this.IDmaterial = IDmaterial;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof RentMaterial){
            RentMaterial r = (RentMaterial) o;
            return r.IDmaterial.equals(this.IDmaterial);
        }
        return false;
    }
}
