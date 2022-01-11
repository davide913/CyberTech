package it.unive.cybertech;

import static it.unive.cybertech.utils.CachedUser.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import it.unive.cybertech.assistenza.HomePageNegative;
import it.unive.cybertech.assistenza.HomePagePositive;
import it.unive.cybertech.messages.MessageService;
import it.unive.cybertech.noleggio.ExpiredRents;

/**
 * This is the first class invoked after the splash screen
 * It manage all the other fragment and the slide panel (hamburger menu)
 * It sets the user's profile info to it
 *
 * @author Mattia Musone
 * */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Menu menu;

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
        View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.user_name)).setText(user.getName() + " " + user.getSurname());
        ImageView profilePicture = header.findViewById(R.id.profile_picture);
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        ((TextView) header.findViewById(R.id.user_email)).setText(fUser.getEmail());
        if (fUser.getPhotoUrl() != null)
            Picasso.get().load(fUser.getPhotoUrl()).into(profilePicture);

        profilePicture.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MessageService.NotificationType type = null;
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("MAIN", "Key: " + key + " Value: " + value);
                if (key.equals("open")) {
                    type = MessageService.NotificationType.valueOf(value);
                }
            }
        }
        if (type != null)
            switch (type) {
                default:
                case base:
                    break;
                case coronavirus:
                    openSection(R.id.nav_menu_covid);
                    break;
                case assistance_chat:
                    openSection(R.id.nav_menu_quarantine_assistance);
                    break;
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.close();
        openSection(item.getItemId());
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_main_menu_notification_showcase:
                startActivity(new Intent(this, ExpiredRents.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This function opens the relative menu base on the provided id
     * only for the rest section, it shows an icon
     *
     * @param id The id of the section to open
     * */
    @SuppressLint("NonConstantResourceId")
    private void openSection(int id) {
        MenuItem item = null;
        if (menu != null)
            item = menu.findItem(R.id.nav_main_menu_notification_showcase);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (id) {
            case R.id.nav_menu_covid:
                if (item != null)
                    item.setVisible(false);
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.gestione_covid.HomePage()).commit();
                break;
            case R.id.nav_menu_quarantine_assistance:
                if (item != null)
                    item.setVisible(false);
                if (user.getPositiveSince() == null)
                    ft.replace(R.id.main_fragment_content, new HomePageNegative()).commit();
                else
                    ft.replace(R.id.main_fragment_content, new HomePagePositive()).commit();
                break;
            case R.id.nav_menu_showcase:
                if (item != null)
                    item.setVisible(true);
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.noleggio.HomePage()).commit();
                break;
            case R.id.nav_menu_groups:
                if (item != null)
                    item.setVisible(false);
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.groups.HomePage()).commit();
                break;
        }
    }
}