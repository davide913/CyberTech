package Profile;

import com.google.cloud.firestore.CollectionReference;

public class RentMaterial {
    private CollectionReference IDmaterial;

    public RentMaterial(){}

    public RentMaterial(CollectionReference IDmaterial) {
        this.IDmaterial = IDmaterial;
    }

    public CollectionReference getIDmaterial() {
        return IDmaterial;
    }

    private void setIDmaterial(CollectionReference IDmaterial) {
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
