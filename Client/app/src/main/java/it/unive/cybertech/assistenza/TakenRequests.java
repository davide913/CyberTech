package it.unive.cybertech.assistenza;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import it.unive.cybertech.R;

/*
Qui devo visualizzare le richieste che ho accettato, poterne vedere i dettagli, andare su ciascuna chat e avere un tasto che mi
permette di chiudere il task una volta completato o di rinunciare al task, lasciando ad un'altro utente la possibilità
di prenderlo in carico
 */

public class TakenRequests extends AppCompatActivity {
    private List<RequestDetails> takenRequestsList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_requests);
    }
}
