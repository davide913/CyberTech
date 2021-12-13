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

        String callerClass = getIntent().getStringExtra("changeFields"); //per capire se mi chiama la RequestViz o la HomePage
        findViewById(R.id.uploadRequest).setOnClickListener(view -> {
            if(callerClass.equals("false")) {
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
                for (AssistanceType a : buffertType) {
                    if (a.getType().equals(type[0]))
                        choosen = a;
                }

                //TODO modificare i campi lat e long
                //TODO : Trovare modo di far inserire all'utente non una stringa ma un GeoPoint, o convertire la stringa in GeoPoint
                /*try {
                    QuarantineAssistance.createQuarantineAssistance(choosen, et_requestTitle.toString(), et_requestText.toString(), CachedUser.user, date, 5, 5);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }*/
                finish();
            }
            else {
                /*String id = getIntent().getStringExtra("id");
                QuarantineAssistance thisAssistance;
                try {
                    thisAssistance = QuarantineAssistance.getQuarantineAssistance(id);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }*/

                //if(!et_requestTitle.toString().equals(thisAssistance.getTitle())) //TODO: quando il Db sarà pronto, chiamare le varie update
                    //thisAssistance.updateTitle(et_requestTitle.toString());

                //if( et_requestText.toString().equals(thisAssistance.getDescription()))
                    //thisAssistance.updateDescription(et_requestText.toString());
            }
        });
    }
}
