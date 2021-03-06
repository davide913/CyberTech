package it.unive.cybertech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.collect.Collections2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.messages.MessageService;
import it.unive.cybertech.signup.LogInActivity;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;

/**
 * This is the first class launched by the system
 * It checks if an user is logged in otherwise it opens the login section.
 *
 * It capture also an intent, provided by the Firebase classes, that manage the notification click.
 *
 * If the user is the first time that use the app, it also add the device id to the user's devices in the database
 *
 * @author Mattia Musone
 * */
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
        //Filter, if any, the intent by the Firebase notification click
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
            Utils.executeAsync(() -> User.obtainUserById(currentUser.getUid()), new Utils.TaskResult<User>() {
                @Override
                public void onComplete(User result) {
                    if (result != null) {
                        CachedUser.user = result;
                        SharedPreferences sh = getPreferences(Context.MODE_PRIVATE);
                        String deviceID = Settings.Secure.ANDROID_ID;
                        Thread t = new Thread(() -> {
                            try {
                                //add this phone id
                                if (sh.getBoolean("FirstTime", true) || Collections2.filter(result.obtainMaterializedDevices(), d -> d.getDeviceId().equals(deviceID)).size() == 0) {
                                    sh.edit().putBoolean("FirstTime", false).apply();
                                    MessageService.getCurrentToken(task -> {
                                        if (task.isSuccessful()) {
                                            Thread t2 = new Thread(()->{
                                                result.addDevice(task.getResult(), deviceID);
                                            });
                                            t2.start();
                                            try {
                                                t2.join();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        t.start();
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        if (finalType != null) {
                            i.putExtra("open", finalType.toString());
                            Log.d("SPLASH SCREEN", "Main activity should open: " + finalType);
                        }
                        startActivity(i);
                    } else {
                        Utils.logout(getApplicationContext());
                        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } else
            startActivity(new Intent(this, LogInActivity.class));
    }
}