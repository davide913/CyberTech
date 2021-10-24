import com.google.api.client.json.Json;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.GeoPoint;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Collection;

class Dispositivi{
    private String id;

    public Dispositivi(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

class Prestiti_in_corso{
    private CollectionReference IDmAteriale;
    private DateTime DataScadenza;

    public Prestiti_in_corso(CollectionReference IDmAteriale, DateTime dataScadenza) {
        this.IDmAteriale = IDmAteriale;
        DataScadenza = dataScadenza;
    }

    public CollectionReference getIDmAteriale() {
        return IDmAteriale;
    }

    public DateTime getDataScadenza() {
        return DataScadenza;
    }
}

class Richieste_prolungamento{
    private CollectionReference IDprestito;

    public Richieste_prolungamento(CollectionReference IDprestito) {
        this.IDprestito = IDprestito;
    }

    public CollectionReference getIDprestito() {
        return IDprestito;
    }
}

class Materiale_noleggio{
    private CollectionReference IDmateriale;

    public Materiale_noleggio(CollectionReference IDmateriale) {
        this.IDmateriale = IDmateriale;
    }

    public CollectionReference getIDmateriale() {
        return IDmateriale;
    }
}

public class Utenti {
    private String id;
    private String Nome;
    private String Cognome;
    private Character Sesso;
    private String Indirizzo;
    private String Citta;
    private String Stato;
    private GeoPoint Posizione;
    private boolean Greenpass;
    private DateTime PositivoDal;
    private int PunteggioPrestito;
    private Collection<Dispositivi> Dispositivi;
    private Collection<Prestiti_in_corso> PrestitiInCorso;
    private Collection<Richieste_prolungamento> RichiesteProlungamento;
    private Collection<Materiale_noleggio> MaterialeNoleggio;
    private Json Assistenza;

    public Utenti(String id, String nome, String cognome, Character sesso, String indirizzo, String citta, String stato, GeoPoint posizione, boolean greenpass, DateTime positivoDal, int punteggioPrestito, Collection<Dispositivi> dispositivi, Collection<Prestiti_in_corso> prestitiInCorso, Collection<Richieste_prolungamento> richiesteProlungamento, Collection<Materiale_noleggio> materialeNoleggio, Json assistenza) {
        this.id = id;
        Nome = nome;
        Cognome = cognome;
        Sesso = sesso;
        Indirizzo = indirizzo;
        Citta = citta;
        Stato = stato;
        Posizione = posizione;
        Greenpass = greenpass;
        PositivoDal = positivoDal;
        PunteggioPrestito = punteggioPrestito;
        Dispositivi = dispositivi;
        PrestitiInCorso = prestitiInCorso;
        RichiesteProlungamento = richiesteProlungamento;
        MaterialeNoleggio = materialeNoleggio;
        Assistenza = assistenza;
    }


}
