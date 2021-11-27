package it.unive.cybertech.signup;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String address = "", city = "", country = "";
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = gcd.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        Address adr = addresses.get(0);
                        city = adr.getLocality();
                        country = adr.getCountryName();
                        address = adr.getThoroughfare();
                    }
                    User u = User.createUser(task.getResult().getUser().getUid(), name, surname, Utils.convertToSex(sex), address, city, country, (long) location.getLatitude(), (long) location.getLongitude(), false);
                    if(u != null) {
                        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    Utils.showGenericDialog("Registrazione fallita", "Usa una password più sicura", c);
                    e.printStackTrace();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    Utils.showGenericDialog("Registrazione fallita", "Indirizzo email malformato", c);
                } catch (FirebaseAuthUserCollisionException e) {
                    Utils.showGenericDialog("Registrazione fallita", "Utente già registrato", c);
                } catch (Exception e) {
                    Utils.showGenericDialog("Registrazione fallita", "Errore generico", c);
                }
            }
        });
    }

}