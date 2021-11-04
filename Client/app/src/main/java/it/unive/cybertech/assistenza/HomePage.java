package it.unive.cybertech.assistenza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;

/*
La home page di assistenza si occupa di mostrare le richieste in primo piano (in un qualche ordine)
 e di indirizzare l'utente verso le altre view a seconda se vuole creare una nuova richiesta, visualizzare
 quelle già create, andare sul suo profilo
 */

public class HomePage extends AppCompatActivity {
    //Da inserire nel manifest tutti i pulsanti


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_assistenza);

        initViews();
    }

    private void initViews(){
        RecyclerView listRequests = findViewById(R.id.listRequests);
        ArrayList<String> provaLista = new ArrayList<>();
        ListAdapter adapter;    //da sostituire con la mia classe, con tutte le ripercussioni nel codice.

        ArrayList<String> sefunziona = new ArrayList<>();
        sefunziona.add("Ciao");
        sefunziona.add("come");
        sefunziona.add("stai");

        View foo = findViewById(R.id.list_proto);
        Intent Int = new Intent(this, HomePage.class);

        //provaLista.add(foo, sefunziona.get(0));

        provaLista.add(1, "Come");
        provaLista.add(2, "stai");
        provaLista.add("Mike");

        listRequests.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(this, provaLista);
        //Deve tornare in maniera visiva la richiesta di aiuto con titolo, data e location,

        listRequests.setAdapter(adapter);
        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation

        listRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRequestVisualisation();
            }
        });

        /*
        findViewById(R.id.request_proto).setOnClickListener(view -> {
            startActivity(new Intent(this, RequestViz.class));
        });
        */

        //visibile solo se l'utente è positivo, quindi abilitato a chiedere aiuto alla community
        //qui estraggo lo user dal DB

        //il profilo è visibile da tutti
        findViewById(R.id.myProfile).setOnClickListener(view -> {
            startActivity(new Intent(this, MyProfile.class));
        });

        //Poter prendere in carico una richiesta solo se sei negativo
        findViewById(R.id.takenRequests).setOnClickListener(view -> {
            startActivity(new Intent(this, TakenRequests.class));
        });

        //Devo creare l'utente e far vedere questo tasto solo se il flag positivity è true
        //My requests e newHelpRequest sono disponibili solo se sei positivo
        findViewById(R.id.buttMyRequests).setOnClickListener(view -> {
            startActivity(new Intent(this, MyRequests.class));
        });

        findViewById(R.id.newHelpRequest).setOnClickListener(view -> {
            startActivity(new Intent(this, RequestDetails.class));
        });

    }

    public void goToRequestVisualisation() {
        Intent openReqVis = new Intent(this, RequestViz.class);
        startActivity(openReqVis);
    }

}