package it.unive.cybertech.noleggio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.unive.cybertech.R;

public class ProductDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_noleggio);
        Intent i = getIntent();
        String id = i.getStringExtra("ID");
        if (id == null || id.isEmpty())
            finish();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Dettaglio: " + id);
        ImageView photo = findViewById(R.id.showcase_details_image);
        TextView title = findViewById(R.id.title_details_showcase),
                description = findViewById(R.id.description_details_showcase),
                date = findViewById(R.id.expiring_date_details_showcase);
        title.setText("Titolo descrittivo");
        description.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
        Date expire = new Date();
        date.setText(new SimpleDateFormat("dd/MM/yyyy").format(expire));
        FloatingActionButton confirm = findViewById(R.id.confirm_rent_showcase);
        confirm.setOnClickListener(view -> {
            new AcceptResponsibilityDialog().show(getSupportFragmentManager(), "AcceptResponsibilityDialog");
        });
    }

    public static class AcceptResponsibilityDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.rent_disclosure_description)
                    .setTitle(R.string.rent_disclosure)
                    .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                        // FIRE ZE MISSILES!
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                        // User cancelled the dialog
                    });
            return builder.create();
        }
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