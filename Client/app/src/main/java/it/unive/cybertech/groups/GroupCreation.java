package it.unive.cybertech.groups;

import static it.unive.cybertech.database.Groups.Group.CreateGroup;
import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Utils.logout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Groups.Group;


public class GroupCreation extends AppCompatActivity {
    private final @NonNull Context context = this;
    private EditText name;
    private EditText description;
    private Group newGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        setContentView(R.layout.activity_create_group);
        name = findViewById(R.id.create_group_name);
        description = findViewById(R.id.create_group_description);

        @NonNull FloatingActionButton done = findViewById(R.id.create_group_done);
        done.setOnClickListener(v -> {
            if(checkFields())
                createFSGroup();
        });
    }

    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_group);
    }

    private void createFSGroup() {
        @NonNull Thread t = new Thread(() -> {
            try {
                newGroup = CreateGroup(name.getText().toString(), description.getText().toString(), user);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        showShortToast(getString(R.string.GroupCreationDone));
        @NonNull Handler handler = new Handler();
        handler.postDelayed(()-> {
            @NonNull Intent intent = new Intent(context, GroupHomePage.class);
            intent.putExtra("ID", newGroup.getId());
            context.startActivity(intent);
        }, 800);
    }

    private boolean checkFields() {
        boolean ok = true;
        if(name.getText().length() <= 0) {
            ok = false;
            name.setError(getString(R.string.requiredField));
        }
        if (description.getText().length() <= 0) {
            ok = false;
            description.setError(getString(R.string.requiredField));
        }
        return ok;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Useful function that create and show a short-length toast (@see "{@link Toast}".
     * @since 1.0
     */
    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}