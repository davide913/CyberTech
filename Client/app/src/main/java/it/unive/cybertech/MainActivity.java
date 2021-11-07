package it.unive.cybertech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.internal.Constants;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.gestione_covid.HomePage;
import it.unive.cybertech.gestione_covid.ManifestNegativityFragment;
import it.unive.cybertech.gestione_covid.ManifestPositivityFragment;
import it.unive.cybertech.gestione_covid.PosReportedFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.navigationView_MainActivity);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        drawer = findViewById(R.id.drawer_main_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        //initViews();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.close();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_menu_covid:
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.gestione_covid.HomePage()).commit();
                break;
            case R.id.nav_menu_quarantine_assistance:
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.assistenza.HomePage()).commit();
                break;
            case R.id.nav_menu_showcase:
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.noleggio.HomePage()).commit();
                break;
        }

        return false;
    }
}