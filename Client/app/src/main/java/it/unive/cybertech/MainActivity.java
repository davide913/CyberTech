package it.unive.cybertech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.gestioneCovid).setOnClickListener(view -> {
            startActivity(new Intent(this, it.unive.cybertech.gestione_covid.HomePage.class));
        });

        findViewById(R.id.assistenza).setOnClickListener(view -> {
            startActivity(new Intent(this, it.unive.cybertech.assistenza.HomePage.class));
        });

        findViewById(R.id.noleggio).setOnClickListener(view -> {
            startActivity(new Intent(this, it.unive.cybertech.noleggio.HomePage.class));
        });
    }
}