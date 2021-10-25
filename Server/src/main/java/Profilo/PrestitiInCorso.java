package Profilo;

import com.google.cloud.firestore.CollectionReference;
import com.google.type.DateTime;

class PrestitiInCorso{

    private CollectionReference IDmateriale;
    private DateTime DataScadenza;

    public PrestitiInCorso(CollectionReference IDmAteriale, DateTime dataScadenza) {
        this.IDmateriale = IDmAteriale;
        DataScadenza = dataScadenza;
    }

    public CollectionReference getIDmAteriale() {
        return IDmateriale;
    }

    public DateTime getDataScadenza() {
        return DataScadenza;
    }
}
