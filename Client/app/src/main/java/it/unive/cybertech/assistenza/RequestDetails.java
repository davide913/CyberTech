package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import it.unive.cybertech.R;

/*
La classe RequestDetails permette all'utente di creare una richiesta di assistenza, secificando il titolo e in un box dedicato
il testo della richiesta con le prime specifiche. A seguire si potrà aprire una chat privata tra i due utenti, i quali potranno
scambiarsi informazioni più dettagliate
-> Le richieste devono avere dei pulsanti apply, (cleared), delete
-> pulsante chat visibile una volta che la richiesta di apply è stata confermata dal richiedente
 */

public class RequestDetails extends AppCompatActivity {
    private String requestDetails;
    private String requestTitle;
    private String date;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        setTitle("Dettagli Richiesta");
        //onPause() Devo mettere in pausa l'altra activity?
        //Devo mettere dei setter per i campi da collegare poi con il box del testo?

        //collegare i testi con il codice


        Button backNoticeBoard = findViewById(R.id.backNoticeBoard);
        backNoticeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNoticeBoard();
                finish();   //per finire l'attività
            }
        });

        Button uploadRequest = findViewById(R.id.uploadRequest);
        uploadRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //come mando indietro le info che prendo dall'utente
                goToNoticeBoard();
            }
        });
    }

    //Se ho del contenuto in sospeso, creo una bozza o elimino tutto? pop-up per avvertire il cliente che
    //non ha salvato la richiesta di aiuto?
    public void goToNoticeBoard() {
        Intent openNoticeBoard = new Intent(this, HomePage.class);
        startActivity(openNoticeBoard); //devo sostituire start con onResume()?
    }
}
