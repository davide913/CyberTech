package Groups;

import com.google.cloud.firestore.CollectionReference;

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
