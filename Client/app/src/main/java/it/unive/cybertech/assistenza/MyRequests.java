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
import it.unive.cybertech.database.Profile.QuarantineAssistance;

/*
Qui ogni utente potrà vedere le proprie richieste pubblicate, avrà la possibilità di editarle, cancellarle, e vedere
gli utenti che si sono offerti per l'aiuto
 */

public class MyRequests extends AppCompatActivity {
    ListView listMyView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        Toolbar toolbar = findViewById(R.id.toolbar_myRequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("La mia richiesta");

        ArrayList<QuarantineAssistance> myUploadedRequests = new ArrayList<>();
        listMyView = findViewById(R.id.list_MyRequests);
        ArrayAdapter<QuarantineAssistance> adapter;


        adapter = new CastomRequestsAdapter(this, 0, myUploadedRequests);
        listMyView.setAdapter(adapter);

        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        listMyView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(this, RequestViz.class);
            newIntent.putExtra("title", adapter.getItem(position).getTitle());
            newIntent.putExtra("location", adapter.getItem(position).getLocation().toString());
            //newIntent.putExtra("date", adapter.getItem(position).getDateDeliveryDate().toString());

            newIntent.putExtra("class", "positive");
            startActivity(newIntent);
        }));
    }
}
