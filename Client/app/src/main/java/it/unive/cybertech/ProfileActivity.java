package it.unive.cybertech;

import static it.unive.cybertech.utils.CachedUser.user;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;




public class ProfileActivity extends AppCompatActivity {
    private Location location;
    private FusedLocationProviderClient fusedLocationClient;
    Map<String, EditText> editTexts = new HashMap(); // editTexts container
    FloatingActionButton editInfo;                   // edit button
    Context context = ProfileActivity.this;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.profile);

        editInfo = findViewById(R.id.profile_editInfo);

        /* User's info shown in ProfileActivity (read only) */

        EditText name = findViewById(R.id.profile_name);
        name.setText(user.getName());
        editTexts.put("Name", name);

        EditText surname = findViewById(R.id.profile_surname);
        surname.setText(user.getSurname());
        editTexts.put("Surname", surname);

        EditText dateOfBirth = findViewById(R.id.profile_dateOfBirth);
        dateOfBirth.setText(user.getName()); //todo getDateOfBirth al posto di getName()
        editTexts.put("Date", dateOfBirth);

        EditText sex = findViewById(R.id.profile_sex);
        sex.setText(user.getSex().toString().toUpperCase().substring(0, 1));
        editTexts.put("Sex", sex);

        EditText country = findViewById(R.id.profile_country);
        country.setText(user.getCountry());
        editTexts.put("Country", country);

        EditText address = findViewById(R.id.profile_city);
        address.setText(user.getAddress());
        editTexts.put("Address", address);

        EditText city = findViewById(R.id.profile_address);
        city.setText(user.getCity());
        editTexts.put("City", city);


        EditText email = findViewById(R.id.profile_email);
        email.setText(currentUser.getEmail());
        editTexts.put("Email", email);
        email.setOnClickListener(v -> startActivity(new Intent(context, EditEmail.class)));


        EditText pwd = findViewById(R.id.profile_pwd);
        pwd.setText("********");
        editTexts.put("Password", pwd);
        pwd.setOnClickListener( v -> {
            String provider = FirebaseAuth.getInstance().getCurrentUser().getProviderId();
            if(!provider.equals("google.com")) {
                startActivity(new Intent(context, EditPassword.class));
            }
        });


        editInfo.setOnClickListener(v -> {
            initGPS();
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {             // todo nuovi campi location da inserire nel DB
                Address adr = addresses.get(0);

                String newCountry = adr.getCountryName();
                country.setText(newCountry);
                //user.updateCountry(newCountry);        // todo updateCountry()

                String newCity = adr.getLocality();
                city.setText(newCity);
                //user.updateCity(city);                 // todo updateCity()

                String newAddress = adr.getThoroughfare();
                address.setText(newAddress);
                //user.updateAddress(newAddress);        // todo updateAddress()
            }
        });



        /*
        findViewById(R.id.logout).setOnClickListener(v -> {
            Utils.Dialog dialog = new Utils.Dialog(this);
            dialog.setCallback(new Utils.DialogResult() {
                @Override
                public void onSuccess() {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                @Override
                public void onCancel() {

                }
            }).showDialog("Logout","Sei sicuro di voler effettuare il logout?");
        });
         */

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}