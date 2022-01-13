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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

/**
 * Allows the positive user to compile a form with his needs and upload the request
 *
 * @author Mihail Racaru
 * @since 1.1
 */
public class RequestDetails extends AppCompatActivity {
    EditText et_requestTitle, et_requestText, countryReq, cityReq;
    private final @NonNull
    Context context = RequestDetails.this;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private FloatingActionButton editInfo;
    private double latitude, longitude;
    private final User me = user;
    private String type;
    private List<AssistanceType> tList = new ArrayList<>();
    private AssistanceType choosen = null;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        setToolbar();
        findFields();

        Utils.executeAsync(AssistanceType::obtainAssistanceTypes, new Utils.TaskResult<List<AssistanceType>>() {
            @Override
            public void onComplete(List<AssistanceType> result) {
                tList = result;
                ArrayList<String> options = new ArrayList<>();

                for (AssistanceType a : tList) {
                    options.add(a.getType());
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, options);
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        type = (String) parent.getItemAtPosition(position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

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

                    if (!title.isEmpty() && !description.isEmpty() && !countryReq.getText().toString().isEmpty() && !cityReq.getText().toString().isEmpty()) {
                        Utils.Dialog dialog = new Utils.Dialog(context);
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
                                    public void onError(Exception e) {
                                    }
                                });
                            }

                            @Override
                            public void onCancel() {}
                        });
                    } else {
                        message_if_smt_missing();
                    }
                });
            }

            @Override
            public void onError(Exception e) {}
        });
    }

    /**
     * Message thrown at user when a form field is missing
     * @author Mihail Racaru
     * @since 1.1
     */
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
     *
     * @author Mihail Racaru
     * @since 1.1
     */
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_Request);
        setSupportActionBar(toolbar);
        setTitle("Dettagli Richiesta");
    }

    /**
     * Finds all layout elements
     *
     * @author Mihail Racaru
     * @since 1.1
     */
    private void findFields() {
        spinner = findViewById(R.id.spinner_type);
        et_requestTitle = findViewById(R.id.requestTitle);
        et_requestText = findViewById(R.id.requestText);
        editInfo = findViewById(R.id.edit_location);
        countryReq = findViewById(R.id.countryLoc);
        cityReq = findViewById(R.id.cityLoc);
    }

    /**
     * Sets {@link #longitude} , {@link #latitude} and textView {@link #countryReq}, {@link #cityReq}
     *
     * @author Mihail Racaru
     * @since 1.1
     */
    private void updateGPS() {
        try {
            Utils.getLocation(this, new Utils.TaskResult<Utils.Location>() {
                @Override
                public void onComplete(Utils.Location result) {
                    longitude = result.longitude;
                    latitude = result.latitude;
                    countryReq.setText(result.country);
                    cityReq.setText(result.city);
                }

                @Override
                public void onError(Exception e) {
                }
            });
        } catch (Utils.PermissionDeniedException e) {
            e.printStackTrace();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }
}
