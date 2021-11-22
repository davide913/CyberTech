package it.unive.cybertech.gestione_covid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import static it.unive.cybertech.utils.CachedUser.user;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.unive.cybertech.R;

public class ReportPositivityActivity extends AppCompatActivity {
    private Button bSendSign;
    EditText insertDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_positivity);

        initViews();
    }

    private void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar_sendSign);
        bSendSign = findViewById(R.id.button_sendSign);
        insertDate = findViewById(R.id.editText_insertDate);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CreateAlertDialog();

    }



    private void CreateAlertDialog(){
        bSendSign = findViewById(R.id.button_sendSign);
        bSendSign.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Invia segnalazione");
            builder.setMessage("Confermi di voler inviare la segnalazione?\n");
            builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String data = insertDate.getText().toString();
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // here set the pattern as you date in string was containing like date/month/year
                        Date d = sdf.parse(data);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                user.updatePositiveSince(d); //TODO vedere se funziona
                            }
                        }).start();
                    }catch(ParseException ex){
                        // handle parsing exception if date string was different from the pattern applying into the SimpleDateFormat contructor
                    }

                    finish();
                }
            });
            builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        });
    }


}