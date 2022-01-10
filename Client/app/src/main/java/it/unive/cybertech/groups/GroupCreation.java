package it.unive.cybertech.groups;


import static it.unive.cybertech.database.Groups.Group.createGroup;
import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Showables.showShortToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;

/**
 * The activity that allow to create a new group by Families Share (Plugin) users.
 *
 * @author Daniele Dotto
 * @since 1.1
 */
public class GroupCreation extends AppCompatActivity {
    private final @NonNull
    Context context = this;
    private @Nullable
    EditText name;
    private @Nullable
    EditText description;
    private @Nullable
    Group newGroup;
    private @Nullable
    FloatingActionButton done;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initActionBar();
        bindLayoutObjects();

        Objects.requireNonNull(done).setOnClickListener(v -> {
            if (checkFields())
                createFSGroup();
        });
    }

    /**
     * Bind all objects contained in layout.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects() {
        name = findViewById(R.id.create_group_name);
        description = findViewById(R.id.create_group_description);
        done = findViewById(R.id.create_group_done);
    }

    /**
     * Initialize action bar.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.create_group);
    }

    /**
     * Create a new Families Share (Plugin) group.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void createFSGroup() {
        @NonNull Thread t = new Thread(() -> {
            try {
                newGroup = createGroup(getName().getText().toString(), getDescription().getText().toString(), user);
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
        showShortToast(getString(R.string.GroupCreationDone), context);
        @NonNull Handler handler = new Handler();
        handler.postDelayed(() -> {
            @NonNull Intent intent = new Intent(context, GroupHomePage.class);
            intent.putExtra("ID", getNewGroup().getId());
            context.startActivity(intent);
        }, 800);
    }

    /**
     * Check if the required fields (name and description) are filled.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private boolean checkFields() {
        boolean ok = true;
        if (getName().getText().length() <= 0) {
            ok = false;
            getName().setError(getString(R.string.requiredField));
        }
        if (getDescription().getText().length() <= 0) {
            ok = false;
            getDescription().setError(getString(R.string.requiredField));
        }
        return ok;
    }

    /**
     * Manage the 'back button' item
     *
     * @param item The 'back button' item
     * @return true if the current activity "{@link it.unive.cybertech.groups.GroupCreation}" is finished.
     * @author Daniele Dotto
     * @since 1.1
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Return group name EditText only if that is not null.
     *
     * @return "{@link #name}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getName() {
        return Objects.requireNonNull(name);
    }

    /**
     * Return group description EditText only if that is not null.
     *
     * @return "{@link #description}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getDescription() {
        return Objects.requireNonNull(description);
    }

    /**
     * Return the entire new group only if that is not null.
     *
     * @return "{@link #newGroup}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    Group getNewGroup() {
        return Objects.requireNonNull(newGroup);
    }
}