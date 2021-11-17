package it.unive.cybertech.database.Groups.Partecipants;

import com.google.firebase.firestore.CollectionReference;

public class Partecipants {
    private CollectionReference IDUtente;

    public Partecipants(){}

    public Partecipants(CollectionReference IDUtente) {
        this.IDUtente = IDUtente;
    }

    public CollectionReference getIDUtente() {
        return IDUtente;
    }

    private void setIDUtente(CollectionReference IDUtente) {
        this.IDUtente = IDUtente;
    }
}