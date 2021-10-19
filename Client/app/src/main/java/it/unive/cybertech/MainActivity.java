package it.unive.cybertech;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar_MainActivity);
        navigationView = findViewById(R.id.navigationView_MainActivity);

        //Aggiunge il menu ad Hamburger che attivca NavigationView
        DrawerLayout drawer_map_client = findViewById(R.id.drawer_main_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_map_client, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer_map_client.addDrawerListener(toggle);
        toggle.syncState();


        //StartActivity delle varie sezioni
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