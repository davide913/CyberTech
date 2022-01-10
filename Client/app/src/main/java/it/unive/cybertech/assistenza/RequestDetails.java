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
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;

/**
 * Allows the positive user to compile a form with his needs and upload the request
 * @author Mihail Racaru
 * @since 1.1
 */
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
    private  AssistanceType choosen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        setToolbar();
        findFields();

        Spinner spinner = findViewById(R.id.spinner_type);
        ArrayList<String> options = new ArrayList<>();
        ArrayList<AssistanceType> adapterList = new ArrayList<>();

        Thread t = new Thread(() -> {
            try {
                tList = AssistanceType.obtainAssistanceTypes();

                for (AssistanceType a: tList) {
                    options.add(a.getType());
                    adapterList.add(a);
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

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, options);
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        locationRequest();

        editInfo.setOnClickListener(v -> {
            updateGPS();
        });

        findViewById(R.id.uploadRequest).setOnClickListener(view -> {
            Date date = Calendar.getInstance().getTime();
            String title = et_requestTitle.getText().toString();
            String description = et_requestText.getText().toString();


            for (AssistanceType a : tList) {
                if (a.getType().equals(type))
                    choosen = a;
            }

            if(!title.isEmpty() && !description.isEmpty() && !countryReq.getText().toString().isEmpty() && !cityReq.getText().toString().isEmpty()) {
                Utils.Dialog dialog = new Utils.Dialog(this);
                dialog.show(getString(R.string.attention), getString(R.string.request_upload));
                dialog.setCallback(new Utils.DialogResult() {
                                       @Override
                                       public void onSuccess() {
                                           Utils.executeAsync(() -> me.addQuarantineAssistance(choosen, title, description, date, latitude, longitude), new Utils.TaskResult<Boolean>() {
                                               @Override
                                               public void onComplete(Boolean result) {
                                                   setResult(Activity.RESULT_OK);
                                                   finish();
                                               }

                                               @Override
                                               public OnFailureListener onError(Exception e) {
                                                   return null;
                                               }
                                           });
                                       }
                                       @Override
                                       public void onCancel() {

                                       }
                                   });
            }
            else {
                message_if_smt_missing();
            }
        });
    }

    private void message_if_smt_missing() {
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.show(getString(R.string.information), getString(R.string.format_field_empty));
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onCancel() {
            }
        });
    }

    /**
     * Finds and sets the toolbar
     * @author Mihail Racaru
     * @since 1.1
     */
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Request);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Dettagli Richiesta");
    }

    /**
     * Finds all layout elements
     * @author Mihail Racaru
     * @since 1.1
     */
    private void findFields() {
        et_requestTitle = findViewById(R.id.requestTitle);
        et_requestText = findViewById(R.id.requestText);
        editInfo = findViewById(R.id.edit_location);
        countryReq = findViewById(R.id.countryLoc);
        cityReq = findViewById(R.id.cityLoc);
    }

    /**
     * Initialize locationRequest
     * @author Mihail Racaru
     * @since 1.1
     */
    private void locationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(100);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * Sets {@link #longitude} , {@link #latitude} and calles {@link #updateValues}
     * @author Mihail Racaru
     * @since 1.1
     */
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

    /**
     * Sets TextView {@link #countryReq} and {@link #cityReq} from the given location in input
     * @param location from which is computed city and country
     * @author Mihail Racaru
     * @since 1.1
     */
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to show a Toast with the given message String
     *
     * @param message, the input String
     * @author Mihail Racaru
     * @since 1.1
     */
    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
