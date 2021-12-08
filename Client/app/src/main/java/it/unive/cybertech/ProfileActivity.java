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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ProfileActivity extends AppCompatActivity {
    private final @NonNull Context context = ProfileActivity.this;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private final @NonNull FirebaseUser currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    private Map<String, EditText> editTexts;
    private FloatingActionButton editInfo;
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

        editTexts = new HashMap<>();
        editInfo = findViewById(R.id.profile_editInfo);

        name = findViewById(R.id.profile_name);
        name.setText(user.getName());
        editTexts.put("Name", name);

        surname = findViewById(R.id.profile_surname);
        surname.setText(user.getSurname());
        editTexts.put("Surname", surname);

        dateOfBirth = findViewById(R.id.profile_dateOfBirth);
        dateOfBirth.setText(user.getName()); //todo getDateOfBirth al posto di getName()
        editTexts.put("Date", dateOfBirth);

        sex = findViewById(R.id.profile_sex);
        sex.setText(user.getSex().toString().toUpperCase().substring(0, 1));
        editTexts.put("Sex", sex);

        country = findViewById(R.id.profile_country);
        country.setText(user.getCountry());
        editTexts.put("Country", country);

        address = findViewById(R.id.profile_city);
        address.setText(user.getAddress());
        editTexts.put("Address", address);

        city = findViewById(R.id.profile_address);
        city.setText(user.getCity());
        editTexts.put("City", city);

        email = findViewById(R.id.profile_email);
        email.setText(currentUser.getEmail());
        editTexts.put("Email", email);
        email.setOnClickListener(v -> startActivity(new Intent(context, EditEmail.class)));

        pwd = findViewById(R.id.profile_pwd);
        pwd.setText("********");
        editTexts.put("Password", pwd);
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
        editInfo.setOnClickListener(v -> {
            updateGPS();
        });

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
            // user.updateLocationDB(newCountry, newCity, newAddress, latitude, longitude);   // salva l'ultima posizione nel DB todo updateLocationDB()
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

    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}