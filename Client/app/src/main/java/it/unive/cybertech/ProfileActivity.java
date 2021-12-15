package it.unive.cybertech;

import static it.unive.cybertech.utils.CachedUser.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * ProfileActivity is the main activity that allow user to view and edit some personal, account or
 * localization info:
 * - Position can be updated from {@link #updateGPS()}
 * - Email update is manage in "{@link it.unive.cybertech.EditEmail}"
 * - Password update is manage in "{@link it.unive.cybertech.EditPassword}"
 * @author Daniele Dotto
 * @since 1.0
 */
public class ProfileActivity extends AppCompatActivity {
    private final @NonNull Context context = ProfileActivity.this;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private final @NonNull FirebaseUser currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    private FloatingActionButton editInfo, logoutButton;
    private EditText name, surname, dateOfBirth, sex, country, address, city, email, pwd;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.profile);

        setContentView(R.layout.activity_profile);

        editInfo = findViewById(R.id.profile_editInfo);

        name = findViewById(R.id.profile_name);
        name.setText(user.getName());

        surname = findViewById(R.id.profile_surname);
        surname.setText(user.getSurname());

        dateOfBirth = findViewById(R.id.profile_dateOfBirth);
        // @NonNull Date dateOfBirthDB = user.getBirthDayToDate();
        // @NonNull String pattern = "dd/MM/yyyy";
        // @NonNull DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        // @NonNull String dateOfBirthString = df.format(dateOfBirthDB);
        //dateOfBirth.setText(dateOfBirthString); todo registrazione con data di nascita

        sex = findViewById(R.id.profile_sex);
        sex.setText(user.getSex().toString().toUpperCase().substring(0, 1));

        country = findViewById(R.id.profile_country);
        country.setText(user.getCountry());

        address = findViewById(R.id.profile_city);
        address.setText(user.getAddress());

        city = findViewById(R.id.profile_address);
        city.setText(user.getCity());

        email = findViewById(R.id.profile_email);
        email.setText(currentUser.getEmail());
        email.setOnClickListener(v -> startActivity(new Intent(context, EditEmail.class)));

        pwd = findViewById(R.id.profile_pwd);
        pwd.setText("********");
        pwd.setOnClickListener(v -> {
            @NonNull String provider = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderId();
            if (!provider.equals(getString(R.string.googleProvider))) {
                startActivity(new Intent(context, EditPassword.class));
            } else {
                showShortToast(getString(R.string.googleProviderAlert));
            }
        });



        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(100);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        editInfo.setOnClickListener(v -> updateGPS());



        logoutButton = findViewById(R.id.profile_logout);
        logoutButton.setOnClickListener(v -> logout());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            updateGPS();
        } else {
            showShortToast(getString(R.string.positionPrivilegeNeeded));
        }
    }

    /**
     * Function that update GPS coordinates (latitude and longitude);
     * the user is asked to give permission for geolocalisation if they have not been given yet.
     * @since 1.0
     */
    private void updateGPS() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                showShortToast(getString(R.string.localizationUpdated));
                updateValues(location);
            }).addOnFailureListener(e -> {
                Toast.makeText(context, R.string.genericError, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            });
        } else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    /**
     * Function that update EditText values about geolocalisation:
     * @see "{@link #country}"
     * @see "{@link #city}"
     * @see "{@link #address}"
     * @since 1.0
     */
    private void updateValues(@NonNull Location location) {
        @NonNull Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        @NonNull List<Address> addresses;
        try {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            @NonNull String newCountry = addresses.get(0).getCountryName();
            country.setText(newCountry);
            @NonNull String newCity = addresses.get(0).getLocality();
            city.setText(newCity);
            @NonNull String newAddress = addresses.get(0).getThoroughfare();
            address.setText(newAddress);
            // user.updateLocation(newCountry, newCity, newAddress, latitude, longitude);  // todo non funziona @Davide
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Useful function that create and show a short-length toast (@see "{@link Toast}".
     * @since 1.0
     */
    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Logout from application and disconnect user from database access
     * @since 1.0
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        @NonNull Intent intent = new Intent(context, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}