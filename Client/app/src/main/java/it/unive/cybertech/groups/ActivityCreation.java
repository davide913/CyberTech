package it.unive.cybertech.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;

import static it.unive.cybertech.database.Groups.Activity.createActivity;
import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Utils.logout;

public class ActivityCreation extends AppCompatActivity {
    private final @NonNull Context context = this;
    private Group thisGroup;
    private String idGroup;

    private EditText activityName;
    private EditText activityDescription;
    private EditText activityDate;
    private Date date;
    private EditText activityPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        initActionBar();
        bindThisGroup();

        activityName = findViewById(R.id.activityCreation_name);
        activityDescription = findViewById(R.id.activityCreation_description);
        activityDate = findViewById(R.id.activityCreation_date);
        activityPlace = findViewById(R.id.activityCreation_place);


        @NonNull GregorianCalendar calendar = new GregorianCalendar();
        @NonNull String pattern = "dd/MM/yyyy";
        @NonNull DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        activityDate.setOnClickListener(v -> {      // todo: non c'è l'orario ma solo la data
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            @NonNull DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                date = calendar.getTime();
                @NonNull String dateStr = dateFormat.format(date);
                activityDate.setText(dateStr);
            }, y, m, d);
            dialog.show();
        });


        @NonNull FloatingActionButton confirmButton = findViewById(R.id.activityCreation_confirmButton);
        confirmButton.setOnClickListener(v -> {
            if (checkFields())
                createFSactivity();
        });


    }

    private void createFSactivity() {
        try {
            createActivity(activityName.getText().toString(),
                    activityDescription.getText().toString(),
                    activityPlace.getText().toString(),
                    date,
                    user);
            showShortToast(getString(R.string.activityCreated));
            @NonNull Handler handler = new Handler();
            handler.postDelayed(()-> {
                @NonNull Intent i = new Intent(context, GroupDetails.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }, 800);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean checkFields() {
        boolean ok = true;
        if (activityName.getText().length() <= 0) {
            ok = false;
            activityName.setError(getString(R.string.field_required));
        }
        if (activityDescription.getText().length() <= 0) {
            ok = false;
            activityDescription.setError(getString(R.string.field_required));
        }
        if (activityDate.getText().length() <= 0) {
            ok = false;
            activityDate.setError(getString(R.string.field_required));
        }
        if (activityPlace.getText().length() <= 0) {
            ok = false;
            activityPlace.setError(getString(R.string.field_required));
        }
        return ok;
    }

    private void bindThisGroup() {
        Thread t = new Thread(() -> {
            try {
                thisGroup = Group.getGroupById(getIntent().getStringExtra("ID"));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            idGroup = thisGroup.getId();
            if (idGroup == null || idGroup.length() == 0)
                finish();
        });
        t.start();
    }

    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.ActivityCreation);
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