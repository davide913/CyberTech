package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import it.unive.cybertech.R;

public class RequestDetails extends AppCompatActivity {
    private String requestDetails;
    private String requestTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);


        //onPause() Devo mettere in pausa l'altra activity?
        //Devo mettere dei setter per i campi da collegare poi con il box del testo?

        //collegare i testi con il codice


        Button backNoticeBoard = findViewById(R.id.backNoticeBoard);
        backNoticeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNoticeBoard();
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

    public void goToNoticeBoard() {
        Intent openNoticeBoard = new Intent(this, HomePage.class);
        startActivity(openNoticeBoard); //devo sostituire start con onResume()?
    }
}
