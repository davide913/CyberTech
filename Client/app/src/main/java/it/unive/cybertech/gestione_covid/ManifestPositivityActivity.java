package it.unive.cybertech.gestione_covid;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import it.unive.cybertech.R;

public class ManifestPositivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest_positivity);

        initViews();
    }

    private void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar_ManifestPositivity);
        NavigationView navigationView = findViewById(R.id.navigationView_ManifestPositivity);

        //Aggiunge il menu ad Hamburger che attivca NavigationView
        DrawerLayout drawer_map_client = findViewById(R.id.drawer_ManifestPositivity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_map_client, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer_map_client.addDrawerListener(toggle);
        toggle.syncState();
    }
}