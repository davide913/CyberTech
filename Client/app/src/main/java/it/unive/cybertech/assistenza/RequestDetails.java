package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.ProfileActivity;
import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;

/*
Modifica 29/11: aggiunto lo spinner che mi servirà poi come info da mandare al db per avere una lista delle richieste filtrata
 */

public class RequestDetails extends AppCompatActivity {
    EditText et_requestTitle, et_requestLocation, et_requestText;
    //User thisUser = ; //TODO: come passo il current User?
    CachedUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        Toolbar toolbar = findViewById(R.id.toolbar_Request);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Dettagli Richiesta");

        et_requestTitle = findViewById(R.id.requestTitle);
        et_requestText = findViewById(R.id.requestText);
        et_requestLocation = findViewById(R.id.requestLocation);
        final String[] type = new String[1];


        // Get reference of widgets from XML layout
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_type);

        // Initializing a String Array
        String[] options = new String[]{
                "Seleziona una Categoria...",
                "Medicinali",
                "Spesa",
                "Commissioni",
                "Posta"
        };

        final List<String> typeList = new ArrayList<>(Arrays.asList(options));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, typeList){

            @Override
            public boolean isEnabled(int position){
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                type[0] = selectedItemText;
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.uploadRequest).setOnClickListener(view -> {
            //upload tutte le info nel db
            Date date = Calendar.getInstance().getTime();

            ArrayList<AssistanceType> buffertType = null;
            AssistanceType choosen = null;
            try {
                AssistanceType.getAssistanceTypes();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            assert false;
            for (AssistanceType a: buffertType) {
                if(a.getType().equals(type[0]))
                    choosen = a;
            }

            //TODO aggiungere tutti gli altri campi
            try {
                QuarantineAssistance.createQuarantineAssistance(choosen, et_requestTitle.toString(), et_requestText.toString(), user,  date, et_requestLocation.toString());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        });
    }
}
