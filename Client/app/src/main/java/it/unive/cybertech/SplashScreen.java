package it.unive.cybertech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Thread(() -> {
            //User u = User.createUser("Mario", "Rossi", "M", "Via Torino", "Venezia", "Itala", 10, 10, true);
            try {
                User u = User.getUserById("VSSBtIPMOvIqFIZpYAvt");
                CachedUser.user = u;
                startActivity(new Intent(this, MainActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}