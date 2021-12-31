package it.unive.cybertech.groups.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Group;

import static it.unive.cybertech.database.Groups.Activity.createActivity;
import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Showables.showShortToast;
import static it.unive.cybertech.utils.Utils.HANDLER_DELAY;
import static it.unive.cybertech.utils.Utils.executeAsync;

import it.unive.cybertech.utils.Utils.TaskResult;

/**
 * Activity that allow the group activity creation by users.
 *
 * @author Daniele Dotto
 * @since 1.1
 */
public class ActivityCreation extends AppCompatActivity {
    private final @NonNull
    Context context = this;
    private @Nullable
    Group thisGroup;
    private @Nullable
    String idGroup;
    private @Nullable
    Activity newGroupActivity;
    private @Nullable
    EditText activityName;
    private @Nullable
    EditText activityDescription;
    private @Nullable
    EditText activityDate;
    private @Nullable
    Date date;
    private @Nullable
    EditText activityPlace;
    private @Nullable
    FloatingActionButton confirmButton;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        initActionBar();
        bindThisGroup();
        bindLayoutObjects();

        @NonNull GregorianCalendar calendar = new GregorianCalendar();
        @NonNull String pattern = "dd/MM/yyyy";
        @NonNull DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        getActivityDate().setOnClickListener(v -> {      // todo: non c'è l'orario ma solo la data
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            @NonNull DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                date = calendar.getTime();
                @NonNull String dateStr = dateFormat.format(date);
                getActivityDate().setText(dateStr);
            }, y, m, d);
            dialog.show();
        });

        Objects.requireNonNull(confirmButton).setOnClickListener(v -> {
            if (checkFields())
                createFSactivity();
        });
    }

    /**
     * Bind all object contained in this layout.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects() {
        activityName = findViewById(R.id.activityCreation_name);
        activityDescription = findViewById(R.id.activityCreation_description);
        activityDate = findViewById(R.id.activityCreation_date);
        activityPlace = findViewById(R.id.activityCreation_place);

        confirmButton = findViewById(R.id.activityCreation_confirmButton);
    }

    /**
     * Create a new group activity in Families Share (Plugin) with given fields.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void createFSactivity() {
        executeAsync(() -> createActivity(getActivityName().getText().toString(),
                getActivityDescription().getText().toString(),
                getActivityPlace().getText().toString(),
                getDate(),
                user), new TaskResult<Activity>() {
            @Override
            public void onComplete(@NonNull Activity result) {
                newGroupActivity = result;
                executeAsync(() -> getThisGroup().addActivity(newGroupActivity), null);
            }

            @Override
            public void onError(@NonNull Exception e) {
                try {
                    throw new Exception(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        showShortToast(getString(R.string.activityCreated), context);
        @NonNull Handler handler = new Handler();
        handler.postDelayed(() -> {
            @NonNull Intent i = new Intent(context, ActivityDetails.class);
            i.putExtra("ID", idGroup);
            i.putExtra("ID_GroupActivity", getNewGroupActivity().getId());
            startActivity(i);
        }, HANDLER_DELAY);
    }

    /**
     * Check if all fields are filled.
     *
     * @return true if all fields are filled
     * @author Daniele Dotto
     * @since 1.1
     */
    private boolean checkFields() {
        boolean ok = true;
        if (getActivityName().getText().length() <= 0) {
            ok = false;
            getActivityName().setError(getString(R.string.field_required));
        }
        if (getActivityDescription().getText().length() <= 0) {
            ok = false;
            getActivityDescription().setError(getString(R.string.field_required));
        }
        if (getActivityDate().getText().length() <= 0) {
            ok = false;
            getActivityDate().setError(getString(R.string.field_required));
        }
        if (getActivityPlace().getText().length() <= 0) {
            ok = false;
            getActivityPlace().setError(getString(R.string.field_required));
        }
        return ok;
    }

    /**
     * Bind the current group in field "{@link #thisGroup}" using ID passed by previous fragment.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindThisGroup() {
        @NonNull String id = getIntent().getStringExtra("ID");
        executeAsync(() -> Group.getGroupById(id), new TaskResult<Group>() {
            @Override
            public void onComplete(@NonNull Group result) {
                thisGroup = result;
                idGroup = getThisGroup().getId();
                if (idGroup == null || idGroup.length() == 0)
                    finish();
            }

            @Override
            public void onError(@NonNull Exception e) {
                try {
                    throw new Exception(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialize action bar.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.ActivityCreation);
    }

    /**
     * Return the name EditText (only if that is not null).
     *
     * @return "{@link #activityName}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getActivityName() {
        return Objects.requireNonNull(activityName);
    }

    /**
     * Return the description EditText (only if that is not null).
     *
     * @return "{@link #activityDescription}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getActivityDescription() {
        return Objects.requireNonNull(activityDescription);
    }

    /**
     * Return the date EditText (only if that is not null).
     *
     * @return "{@link #activityDate}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getActivityDate() {
        return Objects.requireNonNull(activityDate);
    }

    /**
     * Return the place EditText (only if that is not null).
     *
     * @return "{@link #activityPlace}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getActivityPlace() {
        return Objects.requireNonNull(activityPlace);
    }

    /**
     * Return the Date added by user (only if that is not null).
     *
     * @return "{@link #date}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    Date getDate() {
        return Objects.requireNonNull(date);
    }

    /**
     * Return the entire selected group where user is creating the group activity (only if that is not null).
     *
     * @return "{@link #thisGroup}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    Group getThisGroup() {
        return Objects.requireNonNull(thisGroup);
    }

    /**
     * Return the new group activity (only if that is not null).
     *
     * @return "{@link #newGroupActivity}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    Activity getNewGroupActivity() {
        return Objects.requireNonNull(newGroupActivity);
    }
}