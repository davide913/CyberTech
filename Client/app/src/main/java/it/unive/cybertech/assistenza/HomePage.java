package it.unive.cybertech.assistenza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;

public class HomePage extends AppCompatActivity {
    private List<RequestDetails> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_assistenza);
        requestList = new ArrayList<>();

        Button newHelpRequest = findViewById(R.id.newHelpRequest);
        newHelpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRequestDetails();
            }
        });
    }

    public void goToRequestDetails() {
        Intent openRequestDetailsWindow = new Intent(this, RequestDetails.class);
        startActivity(openRequestDetailsWindow);
    }

}