package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import it.unive.cybertech.R;

/*
La classe RequestDetails permette all'utente di creare una richiesta di assistenza, secificando il titolo e in un box dedicato
il testo della richiesta con le prime specifiche. A seguire si potrà aprire una chat privata tra i due utenti, i quali potranno
scambiarsi informazioni più dettagliate
-> Le richieste devono avere dei pulsanti apply, (cleared), delete
-> pulsante chat visibile una volta che la richiesta di apply è stata confermata dal richiedente
 */

public class RequestDetails extends AppCompatActivity {
    List<RequestInfo> requestInfoList;
    ReferencedClass reference = (ReferencedClass) this.getApplication();
    EditText et_requestTitle, et_requestLocation, et_requestDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        Toolbar toolbar = findViewById(R.id.toolbar_Request);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Dettagli Richiesta");

        requestInfoList = reference.getRequestInfoList();

        et_requestTitle = findViewById(R.id.requestTitle);
        et_requestLocation = findViewById(R.id.requestLocation);
        et_requestDate = findViewById(R.id.requestDate);


        findViewById(R.id.uploadRequest).setOnClickListener(view -> {
            //creare un oggetto Request Info
                int nextId = reference.getNextId();
                RequestInfo newRequest = new RequestInfo(nextId, et_requestTitle.getText().toString(), et_requestLocation.getText().toString(), et_requestDate.getText().toString());

                //lo inserisco nella lista delle richieste
                requestInfoList.add(newRequest);
                reference.setNextId(nextId++);

                Intent intent = new Intent(this, HomePage.class);
                startActivity(intent);
        });
    }

    //Se ho del contenuto in sospeso, creo una bozza o elimino tutto? pop-up per avvertire il cliente che
    //non ha salvato la richiesta di aiuto?
}
