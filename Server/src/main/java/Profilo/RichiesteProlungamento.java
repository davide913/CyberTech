package Profilo;

import com.google.cloud.firestore.CollectionReference;

class RichiesteProlungamento {
    private CollectionReference IDprestito;

    public RichiesteProlungamento(CollectionReference IDprestito) {
        this.IDprestito = IDprestito;
    }

    public CollectionReference getIDprestito() {
        return IDprestito;
    }
}
