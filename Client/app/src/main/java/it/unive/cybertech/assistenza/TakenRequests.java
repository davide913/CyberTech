package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;

/*
Qui devo visualizzare le richieste che ho accettato, poterne vedere i dettagli, andare su ciascuna chat e avere un tasto che mi
permette di chiudere il task una volta completato o di rinunciare al task, lasciando ad un'altro utente la possibilità
di prenderlo in carico
 */

public class TakenRequests extends AppCompatActivity {
    /*
    private ArrayList<String> takenRequestsList;
    ListView listTakenView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_requests);

        Toolbar toolbar = findViewById(R.id.toolbar_takenRequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Sto aiutando...");

        takenRequestsList = new ArrayList<String>();
        listTakenView = findViewById(R.id.list_taken_requests);
        ArrayAdapter<String> adapter;
        takenRequestsList.add("Un'altra prova");

        adapter = new CastomRequestsAdapter(this, 0, takenRequestsList);
        listTakenView.setAdapter(adapter);

        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        listTakenView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(this, RequestViz.class);
            newIntent.putExtra("title", adapter.getItem(position));
            startActivity(newIntent);
        }));




    }

     */
}
