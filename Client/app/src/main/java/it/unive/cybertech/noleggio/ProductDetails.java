package it.unive.cybertech.noleggio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.utils.Utils;

public class ProductDetails extends AppCompatActivity {

    private Material material;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_noleggio);
        Intent i = getIntent();
        String id = i.getStringExtra("ID");
        if (id == null || id.isEmpty())
            finish();
        try {
            material = Material.getMaterialById(id);
        } catch (ExecutionException | InterruptedException e) {
            finish();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Dettaglio: " + material.getTitle());
        ImageView photo = findViewById(R.id.showcase_details_image);
        TextView title = findViewById(R.id.title_details_showcase),
                description = findViewById(R.id.description_details_showcase),
                date = findViewById(R.id.expiring_date_details_showcase);
        title.setText(material.getTitle());
        description.setText(material.getDescription());
        Date expire = new Date();
        date.setText(new SimpleDateFormat("dd/MM/yyyy").format(expire));
        FloatingActionButton confirm = findViewById(R.id.confirm_rent_showcase);
        confirm.setOnClickListener(view -> {
            new Utils.Dialog(this)
                    .setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    })
                    .show(getString(R.string.rent_disclosure_description), getString(R.string.rent_disclosure));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}