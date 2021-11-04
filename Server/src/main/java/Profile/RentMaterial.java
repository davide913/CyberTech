package Profile;

import com.google.cloud.firestore.CollectionReference;

class RentMaterial {
    private CollectionReference IDmaterial;

    public RentMaterial(CollectionReference IDmaterial) {
        this.IDmaterial = IDmaterial;
    }

    public CollectionReference getIDmaterial() {
        return IDmaterial;
    }

    private void setIDmaterial(CollectionReference IDmaterial) {
        this.IDmaterial = IDmaterial;
    }
}
