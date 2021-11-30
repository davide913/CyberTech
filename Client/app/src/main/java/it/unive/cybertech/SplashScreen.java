package it.unive.cybertech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.signup.LogInActivity;
import it.unive.cybertech.signup.SignUpActivity;
import it.unive.cybertech.utils.CachedUser;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            new Thread(() -> {
                //User u = User.createUser("Mario", "Rossi", "M", "Via Torino", "Venezia", "Itala", 10, 10, true);
                try {
                    User u = User.getUserById(currentUser.getUid());
                    if (u != null) {
                        CachedUser.user = u;
                        startActivity(new Intent(this, MainActivity.class));
                    } else
                        startActivity(new Intent(this, LogInActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else
            startActivity(new Intent(this, LogInActivity.class));
    }
}