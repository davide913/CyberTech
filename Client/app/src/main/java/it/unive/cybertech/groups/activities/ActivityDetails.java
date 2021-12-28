package it.unive.cybertech.groups.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Group;

import static it.unive.cybertech.utils.CachedUser.user;

public class ActivityDetails extends AppCompatActivity {
    private final @NonNull
    Context context = this;
    private Group thisGroup;
    private Activity thisGroupActivity;
    private FloatingActionButton joinLeftButton;
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        @NonNull Thread t = new Thread(this::bindThisGroupActivity);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initActionBar();

        @NonNull TextView activityGroupName = findViewById(R.id.activityDetails_ActivityName);
        activityGroupName.setText(thisGroupActivity.getName());

        @NonNull TextView activityGroupDescription = findViewById(R.id.activityDetails_ActivityDescription);
        activityGroupDescription.setText(thisGroupActivity.getDescription());

        @NonNull TextView activityGroupDate = findViewById(R.id.activityDetails_ActivityDate);
        @NonNull String pattern = "dd/MM/yyyy";
        @NonNull DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        @NonNull Timestamp timestamp = thisGroupActivity.getDate();
        @NonNull String date = df.format(timestamp.toDate());
        activityGroupDate.setText(date);

        @NonNull TextView activityGroupLocation = findViewById(R.id.activityDetails_ActivityLocation);
        activityGroupLocation.setText(thisGroupActivity.getPlace());

        joinLeftButton = findViewById(R.id.activityDetails_JoinLeftActivity);
        status = checkGroupActivityMember();
        if(!status) {
            setButtonInfoAsNoPartecipant();
        } else {
            setButtonInfoAsPartecipant();
        }
        joinLeftButton.setOnClickListener(v -> {
            if (!status) {
                addGroupActivityPartecipant();
            } else {
                removeGroupActivityPartecipant();
            }
        });

    }

    private void setButtonInfoAsPartecipant() {
        joinLeftButton.setImageResource(R.drawable.ic_baseline_person_remove_24);
        joinLeftButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red_fs)));
    }

    private void setButtonInfoAsNoPartecipant() {
        joinLeftButton.setImageResource(R.drawable.ic_baseline_person_add_24);
        joinLeftButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_green_fs)));
    }

    private void removeGroupActivityPartecipant() {
        if (checkGroupActivityMember()) {
            @NonNull Thread t = new Thread(() -> thisGroupActivity.removePartecipant(user));
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showShortToast(getString(R.string.GroupActivityRemoved));
            setButtonInfoAsNoPartecipant();
            status = false;
        }
    }

    private void addGroupActivityPartecipant() {
        if (checkGroupMember() && !checkGroupActivityMember()) {
            @NonNull Thread t = new Thread(() -> thisGroupActivity.addPartecipant(user));
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showShortToast(getString(R.string.GroupActivitySubscribed));
            setButtonInfoAsPartecipant();
            status = true;
        } else
            showShortToast(getString(R.string.OnlyGroupMembersForActivityJoin));
    }

    private boolean checkGroupMember() {
        final boolean[] stato = {false};
        @NonNull Thread t = new Thread(() -> {
            try {
                if (thisGroup.getMaterializedMembers().contains(user))
                    stato[0] = true;
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
        return stato[0];
    }

    private boolean checkGroupActivityMember() {
        try {
            if (thisGroupActivity.getMaterializedParticipants().contains(user))
                status = true;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }

    private void bindThisGroupActivity() {
        try {
            thisGroup = Group.getGroupById(getIntent().getStringExtra("ID_Group"));
            thisGroupActivity = Activity.getActivityById(getIntent().getStringExtra("ID_GroupActivity"));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        @NonNull String idGroup = thisGroup.getId();
        @NonNull String idGroupActivity = thisGroupActivity.getId();
        if (idGroup == null || idGroup.length() == 0 || idGroupActivity == null || idGroupActivity.length() == 0)
            finish();
    }

    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(thisGroupActivity.getName());
    }

    /**
     * Useful function that create and show a short-length toast (@see "{@link Toast}".
     *
     * @since 1.0
     */
    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}