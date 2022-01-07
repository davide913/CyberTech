package it.unive.cybertech.assistenza;

import static it.unive.cybertech.utils.CachedUser.user;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;


public class RequestDetails extends AppCompatActivity {
    EditText et_requestTitle, et_requestText, countryReq, cityReq;
    private final @NonNull
    Context context = RequestDetails.this;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FloatingActionButton editInfo;
    private LocationRequest locationRequest;
    private double latitude, longitude;
    private final User me = user;
    private String type;
    private  ArrayList<AssistanceType> tList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        setToolbar();
        findFields();

       //lo spinner
        Spinner spinner = findViewById(R.id.spinner_type);
        ArrayList<String> options = new ArrayList<>();
        final ArrayList<AssistanceType> adapterList = new ArrayList<>();

        Utils.executeAsync(AssistanceType::getAssistanceTypes, new Utils.TaskResult<ArrayList<AssistanceType>>() {
            @Override
            public void onComplete(ArrayList<AssistanceType> result) {
                tList = result;
                for (AssistanceType a: tList) {
                    options.add(a.getType());
                    adapterList.add(a);
                }
            }

            @Override
            public void onError(Exception e) {}
        });

        //ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, options);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                type = selectedItemText;

                if(position >= 0){
                    showShortToast("Selected : " + selectedItemText);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        locationRequest();

        editInfo.setOnClickListener(v -> {
            updateGPS();
        });

        findViewById(R.id.uploadRequest).setOnClickListener(view -> {
            //upload tutte le info nel db
            Date date = Calendar.getInstance().getTime();

            //per l'upload
            Thread m = new Thread(() -> {
                try {
                    ArrayList<AssistanceType> buffertType = null;
                    AssistanceType choosen = null;
                    buffertType = AssistanceType.getAssistanceTypes();

                    for (AssistanceType a : buffertType) {
                        if (a.getType().equals(type))
                            choosen = a;
                    }
                    String title = et_requestTitle.getText().toString();
                    String description = et_requestText.getText().toString();
                    me.addQuarantineAssistance(choosen, title, description, date, latitude, longitude);
                    setResult(Activity.RESULT_OK);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            });
            m.start();
            try {
                m.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();

        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Request);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Dettagli Richiesta");
    }

    private void findFields() {
        et_requestTitle = findViewById(R.id.requestTitle);
        et_requestText = findViewById(R.id.requestText);
        editInfo = findViewById(R.id.edit_location);
        countryReq = findViewById(R.id.countryLoc);
        cityReq = findViewById(R.id.cityLoc);
    }

    private void locationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(100);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    private void updateGPS() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                showShortToast(getString(R.string.localizationUpdated));
                longitude = location.getLongitude();
                latitude = location.getLatitude();
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
            countryReq.setText(newCountry);
            @NonNull String newCity = addresses.get(0).getLocality();
            cityReq.setText(newCity);
            // user.updateLocation(newCountry, newCity, newAddress, latitude, longitude);   // salva l'ultima posizione nel DB todo updateLocationDB()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
