package Profilo;

import Connessione.Connessione;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.GeoPoint;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.type.DateTime;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Utenti {
    private String id;
    private String Nome;
    private String Cognome;
    private String Sesso;
    private String Indirizzo;
    private String Citta;
    private String Stato;
    private GeoPoint Posizione;
    private boolean Greenpass;
    private DateTime PositivoDal;
    private int PunteggioPrestito;
    private Collection<Profilo.Dispositivi> Dispositivi;
    private Collection<Profilo.PrestitiInCorso> PrestitiInCorso;
    private Collection<Profilo.RichiesteProlungamento> RichiesteProlungamento;
    private Collection<MaterialeNoleggio> MaterialeNoleggio;
    private AssistenzaQuarantena Assistenza;

    private Utenti(String id, String nome, String cognome, String sesso, String indirizzo,
                  String citta, String stato, GeoPoint posizione, boolean greenpass,
                  int punteggioPrestito) {
        Nome = nome;
        Cognome = cognome;
        Sesso = sesso;
        Indirizzo = indirizzo;
        Citta = citta;
        Stato = stato;
        Posizione = posizione;
        Greenpass = greenpass;
        PunteggioPrestito = punteggioPrestito;
    }

    private Utenti(String id, String nome, String cognome, String sesso, String indirizzo,
                   String citta, String stato, GeoPoint posizione, boolean greenpass,
                   DateTime positivoDal, int punteggioPrestito, Collection<Dispositivi> dispositivi,
                   Collection<PrestitiInCorso> prestitiInCorso, Collection<RichiesteProlungamento> richiesteProlungamento,
                   Collection<MaterialeNoleggio> materialeNoleggio, AssistenzaQuarantena assistenza) {

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

    public static Utenti nuovoUtente(String nome, String cognome, String sesso, String indirizzo,
                                     String citta, String stato, GeoPoint posizione, boolean greenpass) throws IOException, ExecutionException, InterruptedException {

        if(sesso.length()>1 || (!sesso.equals("M") && !sesso.equals("F")))      //controllo della variabile passata come sesso
            return null;

        Connessione c = new Connessione();                  //creazione della connessione
        Firestore db = FirestoreClient.getFirestore();      //creazione dell'oggetto db

        Map<String, Object> nuovoUtente = new HashMap<>();          //creazione della "tabella"
        nuovoUtente.put("nome", nome);
        nuovoUtente.put("cognome", cognome);
        nuovoUtente.put("sesso", sesso);
        nuovoUtente.put("indirizzo", indirizzo);
        nuovoUtente.put("citta", citta);
        nuovoUtente.put("stato", stato);
        nuovoUtente.put("geopoint", posizione);
        nuovoUtente.put("greenpass", greenpass);
        //TODO errore sulla push sul server!
        ApiFuture<DocumentReference> addedDocRef = db.collection("utenti").add(nuovoUtente);        //push sul db

        return new Utenti(addedDocRef.get().getId(), nome, cognome, sesso, indirizzo, citta, stato, posizione,
                                    greenpass, null, 0, new ArrayList<>(), new ArrayList<>(),
                                    new ArrayList<>(), new ArrayList<>(), new AssistenzaQuarantena());

    }


}
