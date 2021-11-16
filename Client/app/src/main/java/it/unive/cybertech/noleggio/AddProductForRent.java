package it.unive.cybertech.noleggio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Duration;

import it.unive.cybertech.R;

public class AddProductForRent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_for_rent);
        FloatingActionButton done = findViewById(R.id.showcase_details_done);
        EditText title = findViewById(R.id.showcase_details_title),
                description = findViewById(R.id.showcase_details_description),
                date = findViewById(R.id.showcase_details_date);
        Spinner type = findViewById(R.id.showcase_details_type);
        ImageView image = findViewById(R.id.showcase_details_image);
        Button loadImage = findViewById(R.id.showcase_details_load_image);
        done.setOnClickListener(v -> {
            boolean formOk = true;
            if(title.length() == 0) {
                formOk = false;
                title.setError("Campo obbligatorio");
            }
            if(description.length() == 0) {
                formOk = false;
                description.setError("Campo obbligatorio");
            }
            if(date.length() == 0) {
                formOk = false;
                date.setError("Campo obbligatorio");
            }
            if(type.getSelectedItem() != null && type.getSelectedItem().toString().length() == 0)
                formOk = false;

            if(formOk){
                Toast.makeText(this, "FORM OK", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}