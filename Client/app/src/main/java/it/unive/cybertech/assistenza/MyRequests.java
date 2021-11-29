package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;

/*
Qui ogni utente potrà vedere le proprie richieste pubblicate, avrà la possibilità di editarle, cancellarle, e vedere
gli utenti che si sono offerti per l'aiuto
 */

public class MyRequests extends AppCompatActivity {
    private ArrayList<String> myUploadedRequests;
    ListView listMyView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests); //TODO scambiarla con request_viz oppure

        Toolbar toolbar = findViewById(R.id.toolbar_myRequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("La mia richiesta");

        myUploadedRequests = new ArrayList<String>();
        listMyView = findViewById(R.id.list_MyRequests);
        ArrayAdapter<String> adapter;
        myUploadedRequests.add("La terza prova ");

        adapter = new CastomRequestsAdapter(this, 0, myUploadedRequests);
        listMyView.setAdapter(adapter);

        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        listMyView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(this, RequestViz.class);
            newIntent.putExtra("title", adapter.getItem(position));
            startActivity(newIntent);
        }));

    }
}
