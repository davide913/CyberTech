package it.unive.cybertech.assistenza;

import static it.unive.cybertech.utils.CachedUser.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

public class RequestViz extends AppCompatActivity {
    List<RequestInfo> requestInfoList;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visualisation);

        Toolbar toolbar = findViewById(R.id.toolbar_RequestViz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Dettagli richiesta");

        QuarantineAssistance request = user.getAssistance();

        TextView textTitle = findViewById(R.id.textTitle);
        TextView text = findViewById(R.id.textFull);
        TextView textLocation = findViewById(R.id.textLocation);
        TextView textDate = findViewById(R.id.textDate);


        String title = getIntent().getStringExtra("title");
        textTitle.setText(title);

        String location = getIntent().getStringExtra("location");
        textLocation.setText(location);

        //if(request.getDescription() != null)
        //    text.setText(request.getDescription());

        text.setText("La prova che non la prende");


        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        //String strDate = dateFormat.format(request.getDeliveryDate());
        //if(request.getDeliveryDate() != null)
        //    textDate.setText(strDate);

        textDate.setText("La data");
//Modificato 29/11: questa pagina ha due funzioni dipendentemente dal fatto che ci acceda un malato o uno sano, quello sano può accettare o smettere di eseguire una richiesta
        //l'utente malato invece può modificare la propria richiesta (dopo controlli lato DB dei vari campi modificati), eliminarla o accedere alla chat
        //manca da mettere i vari set GONE per la visibilità

        //Pulsanti visibili solo dall'utente Negativo che presta soccorso
        this.findViewById(R.id.accept_request).setOnClickListener(v -> {
            //La richiesta viene affidata a me, la posso visualizzare nella mia sezione Taken Requests e tolta dalla lista nella Home
            Utils.showGenericDialog("Operazione confermata!", "Hai preso in carico una richiesta", this);
            //chiamata al Db per fare in modo che la richiesta risulti presa da me e tolta dalla pull completa
            finish();
        });

        this.findViewById(R.id.stop_helping).setOnClickListener(v -> {
            //rinuncio a completare la richiesta, e viene inserita di nuovo nella lista generale Home, viene avvisato l'utente originale
            Utils.showGenericDialog("Attenzione!", "Stai per abbandonare la richiesta", this);
            finish();
        });


        //Pulsanti visibili solo dall'utente positivo che richiede soccorso
        this.findViewById(R.id.btn_changeFields).setOnClickListener(v -> { //per modificare i campi, nel DB verificare e sostituire i campi nuovi con quelli vecchi della stessa richiesta
            startActivity(new Intent(this, RequestDetails.class));
            finish();
        });

        this.findViewById(R.id.btn_deleteRequest).setOnClickListener(v -> { //per eliminare dal Db la richiesta
            finish();
        });

        this.findViewById(R.id.btn_chat).setOnClickListener(v -> { //per spostarsi alla chat locale con chi ha accettato la richiesta
            finish();
        });

    }
}
