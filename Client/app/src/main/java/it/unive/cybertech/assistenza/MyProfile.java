package it.unive.cybertech.assistenza;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import it.unive.cybertech.R;

/*
Il profilo utente mostra i dati generali di ciascun individuo registrato, ne consente la modifica, seppur rispettando alcuni
constraints e mostra uno score dell'utente, un punteggio che da un'idea del successo della persona sull'applicazione,
e in parte della sua affidabilità
*/

public class MyProfile extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_Profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Profilo");
    }
}
