package it.unive.cybertech.database.Groups;

import com.google.firebase.firestore.CollectionReference;

public class Members {
    private CollectionReference IDUtente;

    public Members(){}

    public Members(CollectionReference IDUtente) {
        this.IDUtente = IDUtente;
    }

    public CollectionReference getIDUtente() {
        return IDUtente;
    }

    private void setIDUtente(CollectionReference IDUtente) {
        this.IDUtente = IDUtente;
    }
}
