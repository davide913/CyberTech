package it.unive.cybertech.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initGPS();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton done = findViewById(R.id.signup_done);
        EditText name = findViewById(R.id.name_signup),
                surname = findViewById(R.id.surname_signup),
                email = findViewById(R.id.email_signup),
                pwd = findViewById(R.id.password_signup),
                confirmPwd = findViewById(R.id.password_confirm_signup);
        Spinner sex = findViewById(R.id.sex_signup);
        done.setOnClickListener(v -> {
            boolean ok = true;
            if (name.getText().length() == 0) {
                ok = false;
                name.setError(getString(R.string.field_required));
            }
            if (surname.getText().length() == 0) {
                ok = false;
                surname.setError(getString(R.string.field_required));
            }
            if (email.getText().length() == 0) {
                ok = false;
                email.setError(getString(R.string.field_required));
            }
            if (pwd.getText().length() == 0) {
                ok = false;
                pwd.setError(getString(R.string.field_required));
            }
            if (confirmPwd.getText().length() == 0) {
                ok = false;
                confirmPwd.setError(getString(R.string.field_required));
            }
            if (!pwd.getText().toString().equals(confirmPwd.getText().toString())) {
                ok = false;
                confirmPwd.setError(getString(R.string.password_mismatch));
            }

            if (ok)
                signInWithEmailAndPassword(email.getText().toString(), pwd.getText().toString(), name.getText().toString(), surname.getText().toString(), sex.getSelectedItem().toString(), this);
        });
    }

    private void initGPS() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            this.location = location;
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void signInWithEmailAndPassword(String email, String password, String name, String surname, String sex, Context c) {
        try {
            String address = "", city = "", country = "";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                Address adr = addresses.get(0);
                city = adr.getLocality();
                country = adr.getCountryName();
                address = adr.getThoroughfare();
                String finalAddress = address;
                String finalCity = city;
                String finalCountry = country;
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        new Thread(() -> {
                            try {
                                User u = User.createUser(task.getResult().getUser().getUid(), name.trim(), surname.trim(), Utils.convertToSex(sex), finalAddress, finalCity, finalCountry, (long) location.getLatitude(), (long) location.getLongitude(), false);
                                if (u != null) {
                                    Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            new Utils.Dialog(c).show("Registrazione fallita", "Usa una password più sicura");
                            e.printStackTrace();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            new Utils.Dialog(c).show("Registrazione fallita", "Indirizzo email malformato");
                        } catch (FirebaseAuthUserCollisionException e) {
                            new Utils.Dialog(c).show("Registrazione fallita", "Utente già registrato");
                        } catch (Exception e) {
                            new Utils.Dialog(c).show("Registrazione fallita", "Errore generico");
                        }
                    }
                });
            }else
                new Utils.Dialog(c).show("Registrazione fallita", "Impossibile ottenere l'indirizzo civico");
        }catch(IOException e){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}