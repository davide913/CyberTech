package it.unive.cybertech.gestione_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import it.unive.cybertech.MainActivity;
import it.unive.cybertech.R;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_covid);

        initViews();
    }

    private void initViews(){

        Toolbar toolbar = findViewById(R.id.toolbar_Homepage_covid);
        NavigationView navigationView = findViewById(R.id.navigationView_Homepage_covid);

        //Aggiunge il menu ad Hamburger che attivca NavigationView
        DrawerLayout drawer_map_client = findViewById(R.id.drawer_homepage_covid);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_map_client, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer_map_client.addDrawerListener(toggle);
        toggle.syncState();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.SignPos:
                startActivity(new Intent(this, it.unive.cybertech.gestione_covid.ManifestPositivityActivity.class));
                break;
            case R.id.SignNeg:
                startActivity(new Intent(this, it.unive.cybertech.gestione_covid.ManifestNegativityActivity.class));
                break;
            case R.id.ImpostazioniCovid:
                startActivity(new Intent(this, it.unive.cybertech.gestione_covid.SettingsActivity.class));
                break;
            case R.id.HomeCovid:
                startActivity(new Intent(this, it.unive.cybertech.MainActivity.class));
                break;
        }
        return false;
    }
}
