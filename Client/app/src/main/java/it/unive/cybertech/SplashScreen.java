package it.unive.cybertech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.common.collect.Collections2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Device;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.messages.MessageService;
import it.unive.cybertech.signup.LogInActivity;
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
        MessageService.NotificationType type = null;
        MessageService.initNotificationChannels(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                if (key.equals("type")) {
                    try {
                        type = MessageService.NotificationType.valueOf(value);
                    } catch (Exception e) {
                    }
                }
                Log.d("SPLASH SCREEN", "Key: " + key + " Value: " + value);
            }
        }
        if (currentUser != null) {
            MessageService.NotificationType finalType = type;
            new Thread(() -> {
                try {
                    User u = User.getUserById(currentUser.getUid());
                    if (u != null) {
                        CachedUser.user = u;
                        SharedPreferences sh = getPreferences(Context.MODE_PRIVATE);
                        String deviceID = Settings.Secure.ANDROID_ID;
                        if (sh.getBoolean("FirstTime", true) || Collections2.filter(u.getMaterializedDevices(), d -> d.getDeviceId().equals(deviceID)).size() == 0) {
                            sh.edit().putBoolean("FirstTime", false).apply();
                            MessageService.getCurrentToken(task -> {
                                if (task.isSuccessful()) {

                                    new Thread(() -> {
                                        try {
                                            u.addDevice(Device.createDevice(task.getResult(), deviceID));
                                        } catch (ExecutionException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }).start();
                                }
                            });
                        }
                        Intent i = new Intent(this, MainActivity.class);
                        if (finalType != null) {
                            i.putExtra("open", finalType.toString());
                            Log.d("SPLASH SCREEN", "Main activity should open: " + finalType);
                        }
                        startActivity(i);
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