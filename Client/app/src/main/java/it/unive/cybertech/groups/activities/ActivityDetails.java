package it.unive.cybertech.groups.activities;

import static it.unive.cybertech.groups.activities.GroupActivities.RELOAD_ACTIVITY;
import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Showables.showShortToast;
import static it.unive.cybertech.utils.Utils.executeAsync;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;
import it.unive.cybertech.utils.Utils.TaskResult;

/**
 * Activity that allow to see all group activity details.
 *
 * @author Daniele Dotto
 * @since 1.1
 */
public class ActivityDetails extends AppCompatActivity {
    private final @NonNull
    Context context = this;
    private @Nullable
    Group thisGroup;
    private @Nullable
    Activity thisGroupActivity;
    private @Nullable
    TextView activityGroupName;
    private @Nullable
    TextView activityGroupDescription;
    private @Nullable
    TextView activityGroupDate;
    private @Nullable
    TextView activityGroupLocation;
    private @Nullable
    FloatingActionButton joinLeftButton;
    private boolean status = false;
    private @NonNull
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getIntent().getStringExtra("ID") == null || getIntent().getStringExtra("ID_GroupActivity") == null)
            finish();
        bindLayoutObjects();
        bindThisGroupActivity();
    }

    /**
     * Set TextViews text contained in this layout.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void setTextViews() {
        getActivityGroupName().setText(getThisGroupActivity().getName());
        getActivityGroupDescription().setText(getThisGroupActivity().getDescription());
        @NonNull String date = Utils.formatDateToString(getThisGroupActivity().getDate().toDate());
        getActivityGroupDate().setText(date);
        getActivityGroupLocation().setText(getThisGroupActivity().getPlace());
    }

    /**
     * Bind all object contained in this layout.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects() {
        activityGroupName = findViewById(R.id.activityDetails_ActivityName);
        activityGroupDescription = findViewById(R.id.activityDetails_ActivityDescription);
        activityGroupDate = findViewById(R.id.activityDetails_ActivityDate);
        activityGroupLocation = findViewById(R.id.activityDetails_ActivityLocation);

        joinLeftButton = findViewById(R.id.activityDetails_JoinLeftActivity);
        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Set source image and color (red) for left the group activity.
     *
     * @author Daniele Dotto
     * @see "{@link #joinLeftButton}"
     * @since 1.1
     */
    private void setButtonInfoAsParticipant() {
        getJoinLeftButton().setImageResource(R.drawable.ic_baseline_person_remove_24);
        getJoinLeftButton().setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red_fs)));
    }

    /**
     * Set source image and color (green) for join the group activity.
     *
     * @author Daniele Dotto
     * @see "{@link #joinLeftButton}"
     * @since 1.1
     */
    private void setButtonInfoAsNoParticipant() {
        getJoinLeftButton().setImageResource(R.drawable.ic_baseline_person_add_24);
        getJoinLeftButton().setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_green_fs)));
    }

    /**
     * Remove current user from the current selected group activity "{@link #thisGroupActivity}".
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void removeGroupActivityParticipant() {
        if (checkGroupActivityMember()) {
            Thread t = new Thread(() -> {
                getThisGroupActivity().removeParticipant(user);
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showShortToast(getString(R.string.GroupActivityRemoved), context);
            setButtonInfoAsNoParticipant();
            status = false;
        }
    }

    /**
     * Add current user in the current selected group activity "{@link #thisGroupActivity}".
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void addGroupActivityParticipant() {
        if (checkGroupMember() && !checkGroupActivityMember()) {
            Thread t = new Thread(() -> {
                getThisGroupActivity().addParticipant(user);
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showShortToast(getString(R.string.GroupActivitySubscribed), context);
            setButtonInfoAsParticipant();
            status = true;
        } else
            showShortToast(getString(R.string.OnlyGroupMembersForActivityJoin), context);
    }

    /**
     * Check if current user is a group member of "{@link #thisGroup}".
     *
     * @return true: current user is already member ||| false: current user is not member yet
     * @author Daniele Dotto
     * @since 1.1
     */
    private boolean checkGroupMember() {
        final boolean[] stato = {false};
        Thread t = new Thread(() -> {
            try {
                List<User> tmp = getThisGroup().obtainMaterializedMembers();
                stato[0] = tmp != null && tmp.contains(user);
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

    /**
     * Check if current user is a already a participant of the current activity"{@link #thisGroupActivity}".
     *
     * @return true: current user is already participant ||| false: current user is not participant yet
     * @author Daniele Dotto
     * @since 1.1
     */
    private boolean checkGroupActivityMember() {
        Thread t = new Thread(() -> {
            try {
                List<User> tmp = getThisGroupActivity().obtainMaterializedParticipants();
                status = tmp != null && tmp.contains(user);
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
        return status;
    }

    /**
     * Find the group and group activity in DB based on current selected group.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindThisGroupActivity() {
        executeAsync(() -> Group.obtainGroupById(getIntent().getStringExtra("ID")), new TaskResult<Group>() {
            @Override
            public void onComplete(@NonNull Group result) {
                thisGroup = result;
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
        executeAsync(() -> Activity.obtainActivityById(getIntent().getStringExtra("ID_GroupActivity")), new TaskResult<Activity>() {
            @Override
            public void onComplete(@NonNull Activity result) {
                thisGroupActivity = result;
                initActionBar();
                setTextViews();
                status = checkGroupActivityMember();
                if (user.getPositiveSince() == null || status)
                    getJoinLeftButton().setVisibility(View.VISIBLE);
                if (!status) {
                    setButtonInfoAsNoParticipant();
                } else {
                    setButtonInfoAsParticipant();
                }
                getJoinLeftButton().setOnClickListener(v -> {
                    if (!status) {
                        addGroupActivityParticipant();
                    } else {
                        removeGroupActivityParticipant();
                    }
                    setResult(RELOAD_ACTIVITY);
                    finish();
                });
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
        actionBar.setTitle(getThisGroupActivity().getName());
    }

    /**
     * Manage the 'back button' item
     *
     * @param item The 'back button' item
     * @return true if the current activity "{@link it.unive.cybertech.groups.activities.ActivityDetails}" is finished.
     * @author Daniele Dotto
     * @since 1.1
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Return the entire selected group (only if that is not null).
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
     * Return the entire selected group activity (only if that is not null).
     *
     * @return "{@link #thisGroupActivity}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    Activity getThisGroupActivity() {
        return Objects.requireNonNull(thisGroupActivity);
    }

    /**
     * Return group activity name EditText only if that is not null.
     *
     * @return "{@link #activityGroupName}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    TextView getActivityGroupName() {
        return Objects.requireNonNull(activityGroupName);
    }

    /**
     * Return group activity description EditText only if that is not null.
     *
     * @return "{@link #activityGroupDescription}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    TextView getActivityGroupDescription() {
        return Objects.requireNonNull(activityGroupDescription);
    }

    /**
     * Return group activity date EditText only if that is not null.
     *
     * @return "{@link #activityGroupDate}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    TextView getActivityGroupDate() {
        return Objects.requireNonNull(activityGroupDate);
    }

    /**
     * Return group activity location EditText only if that is not null.
     *
     * @return "{@link #activityGroupLocation}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    TextView getActivityGroupLocation() {
        return Objects.requireNonNull(activityGroupLocation);
    }

    /**
     * Return left/join group activity button only if that is not null.
     *
     * @return "{@link #joinLeftButton}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    FloatingActionButton getJoinLeftButton() {
        return Objects.requireNonNull(joinLeftButton);
    }
}