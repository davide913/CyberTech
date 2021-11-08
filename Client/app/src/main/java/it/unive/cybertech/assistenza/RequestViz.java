package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.List;

import it.unive.cybertech.R;

public class RequestViz extends AppCompatActivity {
    List<RequestInfo> requestInfoList;
    int id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visualisation);

        TextView textTitle = findViewById(R.id.textTitle);
        TextView text = findViewById(R.id.textFull);
        TextView textLocation = findViewById(R.id.textLocation);
        TextView textDate = findViewById(R.id.textDate);

        String title = getIntent().getStringExtra("title");
        textTitle.setText(title);
    }
}
