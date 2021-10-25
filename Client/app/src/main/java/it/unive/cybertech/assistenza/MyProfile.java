package it.unive.cybertech.assistenza;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unive.cybertech.R;

/*
Il profilo utente mostra i dati generali di ciascun individuo registrato, ne consente la modifica, seppur rispettando alcuni
constraints e mostra uno score dell'utente, un punteggio che da un'idea del successo della persona sull'applicazione,
e in parte della sua affidabilità
*/

public class MyProfile extends AppCompatActivity {
    private String name;
    private String surname;
    private String age;
    private String location;
    private String score;
    private boolean positivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
    }
}
