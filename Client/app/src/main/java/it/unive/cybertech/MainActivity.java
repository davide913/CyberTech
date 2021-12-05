package it.unive.cybertech;

import static it.unive.cybertech.utils.CachedUser.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

import it.unive.cybertech.assistenza.HomePageNegative;
import it.unive.cybertech.assistenza.HomePagePositive;
import it.unive.cybertech.messages.MessageService;

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
        MessageService.getCurrentToken(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful())
                    Log.d("MAIN", "TOKEN: "+task.getResult());
                else
                    Log.e("MAIN", "Error retriving token");
            }
        });
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
                ft.replace(R.id.main_fragment_content, new HomePageNegative()).commit();
                break;
            case R.id.nav_menu_showcase:
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.noleggio.HomePage()).commit();
                break;
            case R.id.nav_menu_groups:
                ft.replace(R.id.main_fragment_content, new it.unive.cybertech.groups.HomePage()).commit();
                break;
        }

        return false;
    }
}