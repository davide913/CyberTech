package it.unive.cybertech.assistenza;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import it.unive.cybertech.R;

/*
Qui ogni utente potrà vedere le proprie richieste pubblicate, avrà la possibilità di editarle, cancellarle, e vedere
gli utenti che si sono offerti per l'aiuto
 */

public class MyRequests extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        Toolbar toolbar = findViewById(R.id.toolbar_myRequest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Le mie richieste");
    }
}
