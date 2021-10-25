package it.unive.cybertech.assistenza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private List<RequestDetails> requestList;
    private TextView text;
    private EditText editext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_assistenza);

        requestList = new ArrayList<>();

        //Deve tornare in maniera visiva la richiesta di aiuto con titolo, una preview del testo, data e location,
        //visibile solo se l'utente è positivo, quindi abilitato a chiedere aiuto alla community
        Button newHelpRequest = findViewById(R.id.newHelpRequest);
        newHelpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRequestDetails();
            }
        });

        Button takenRequests = findViewById(R.id.takenRequests);
        takenRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTakenRequests();
            }
        });

        //Devo creare l'utente e far vedere questo tasto solo se il flag positivity è true
        Button myRequests = findViewById(R.id.buttMyRequests);
        myRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyRequests();
            }
        });

        Button myProfile = findViewById(R.id.myProfile);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyProfile();
            }
        });
    }

    public void goToRequestDetails() {
        Intent openRequestDetailsWindow = new Intent(this, RequestDetails.class);
        startActivity(openRequestDetailsWindow);
    }

    public void goToTakenRequests() {
        Intent openTakenRequests = new Intent(this, TakenRequests.class);
        startActivity(openTakenRequests);
    }

    public void goToMyRequests() {
        Intent openMyRequests = new Intent(this, MyRequests.class);
        startActivity(openMyRequests);
    }

    public void goToMyProfile() {
        Intent openProfile = new Intent(this, MyProfile.class);
        startActivity(openProfile);
    }

}