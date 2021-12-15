package it.unive.cybertech.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

public class SignUpActivity extends AppCompatActivity {

    private final @NonNull Context context = this;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;
    private EditText name, surname, dateOfBirth, email, pwd, confirmPwd;
    private Date inputDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        initGPS();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        @NonNull FloatingActionButton done = findViewById(R.id.signup_done);

        name = findViewById(R.id.name_signup);
        surname = findViewById(R.id.surname_signup);
        dateOfBirth = findViewById(R.id.dateOfBirth_signup);
        email = findViewById(R.id.email_signup);
        pwd = findViewById(R.id.password_signup);
        confirmPwd = findViewById(R.id.password_confirm_signup);


        @NonNull GregorianCalendar calendar = new GregorianCalendar();
        @NonNull String pattern = "dd/MM/yyyy";
        @NonNull DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        dateOfBirth.setOnClickListener(v -> {
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            @NonNull DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                inputDateOfBirth = calendar.getTime();
                @NonNull String dateStr = dateFormat.format(inputDateOfBirth);
                dateOfBirth.setText(dateStr);
            }, y, m, d);
            dialog.show();
        });


        @NonNull Spinner sex = findViewById(R.id.sex_signup);
        done.setOnClickListener(v -> {
            if (checkFields())
                signInWithEmailAndPassword(email.getText().toString(), pwd.getText().toString(), name.getText().toString(), surname.getText().toString(), inputDateOfBirth, sex.getSelectedItem().toString());
        });
    }

    private boolean checkFields() {
        boolean ok = true;
        if (name.getText().length() == 0) {
            ok = false;
            name.setError(getString(R.string.field_required));
        }
        if (surname.getText().length() == 0) {
            ok = false;
            surname.setError(getString(R.string.field_required));
        }
        if (dateOfBirth.getText().length() == 0) {
            ok = false;
            dateOfBirth.setError(getString(R.string.field_required));
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
        return ok;
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

    private void signInWithEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull String name, @NonNull String surname, @NonNull Date dateOfBirth, @NonNull String sex) {
        try {
            @NonNull Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            @NonNull List<Address> addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                @NonNull Address adr = addresses.get(0);
                @NonNull String finalCity = adr.getLocality();
                @NonNull String finalCountry = adr.getCountryName();
                @NonNull String finalAddress = adr.getThoroughfare();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        new Thread(() -> {
                            try {
                                @NonNull User u = User.createUser(Objects.requireNonNull(task.getResult().getUser()).getUid(), name.trim(), surname.trim(), Utils.convertToSex(sex), dateOfBirth, finalAddress, finalCity, finalCountry, (long) location.getLatitude(), (long) location.getLongitude(), false);
                                if (u != null) {
                                    @NonNull Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.SaferPwd));
                            e.printStackTrace();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.wrongEmailFormat));
                        } catch (FirebaseAuthUserCollisionException e) {
                            new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.ExistingUser));
                        } catch (Exception e) {
                            new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.genericError));
                        }
                    }
                });
            } else
                new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.LocalizationError));
        } catch (IOException e) {
            e.printStackTrace();
        }
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