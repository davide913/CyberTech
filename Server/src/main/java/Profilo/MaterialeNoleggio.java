package Profilo;

import com.google.cloud.firestore.CollectionReference;

class MaterialeNoleggio {
    private CollectionReference IDmateriale;

    public MaterialeNoleggio(CollectionReference IDmateriale) {
        this.IDmateriale = IDmateriale;
    }

    public CollectionReference getIDmateriale() {
        return IDmateriale;
    }
}
