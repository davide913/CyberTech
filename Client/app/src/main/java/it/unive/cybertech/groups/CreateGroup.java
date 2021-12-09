package it.unive.cybertech.groups;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.unive.cybertech.R;


public class CreateGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_group);
        FloatingActionButton done = findViewById(R.id.create_group_done);
        EditText name = findViewById(R.id.create_group_name),
                description = findViewById(R.id.create_group_description);
        done.setOnClickListener(v -> {
            boolean ok = true;
            if (name.getText().length() == 0)
            {
                ok = false;
                name.setError(getString(R.string.field_required));
            }
            if (description.getText().length() == 0)
            {
                ok = false;
                description.setError(getString(R.string.field_required));
            }
            if(ok){

            }
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