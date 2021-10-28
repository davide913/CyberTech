package it.unive.cybertech.gestione_covid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import it.unive.cybertech.R;

public class ReportPositivityActivity extends AppCompatActivity {
    private EditText mInsertNome, mInsertCognome, mInsertData;
    private Button bSendSign;
    private SwitchCompat mAnonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_positivity);

        initViews();
    }

    private void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar_sendSign);
        bSendSign = findViewById(R.id.button_sendSign);
        mInsertNome = findViewById(R.id.EditText_insertName);
        mInsertCognome = findViewById(R.id.editText_insertSurname);
        mInsertData = findViewById(R.id.editText_insertDate);
        mAnonymous = findViewById(R.id.switch_anonymous);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        isAnonymous();
        CreateAlertDialog();

    }

    private void isAnonymous(){
        mAnonymous.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(mAnonymous.isChecked()) {
                mInsertNome.setVisibility(View.INVISIBLE);
                mInsertCognome.setVisibility(View.INVISIBLE);
            }
            else{
                mInsertNome.setVisibility(View.VISIBLE);
                mInsertCognome.setVisibility(View.VISIBLE);
            }
        }));

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
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
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