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
    //ReferencedClass reference = (ReferencedClass) this.getApplication();
    int id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visualisation);

        TextView textTitle = findViewById(R.id.textTitle);
        TextView text = findViewById(R.id.textFull);
        TextView textLocation = findViewById(R.id.textLocation);
        TextView textDate = findViewById(R.id.textDate);

        //requestInfoList = reference.getRequestInfoList();

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        RequestInfo request = null;

        if(id >= 0) {
            for(RequestInfo r: requestInfoList) {
                if (r.getId() == id)
                    request = r;
            }
            textTitle.setText(request.getTitle());
            text.setText(request.getText());
            textLocation.setText(request.getLocation());
            textDate.setText(request.getDate());
        }
    }
}
