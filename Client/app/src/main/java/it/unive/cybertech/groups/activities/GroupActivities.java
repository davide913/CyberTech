package it.unive.cybertech.groups.activities;

import static it.unive.cybertech.database.Groups.Group.obtainGroupById;
import static it.unive.cybertech.utils.Utils.executeAsync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;
import it.unive.cybertech.utils.Utils.TaskResult;

/**
 * HomePage is a main fragment that allow user to view all group activities for a determinate group.
 * Users can create new group activities:
 *
 * @author Daniele Dotto
 * @see "{@link it.unive.cybertech.groups.activities.ActivityCreation}"
 * @since 1.1
 */
public class GroupActivities extends Fragment implements Utils.ItemClickListener {
    private static final int ACTIVITY_DETAILS_CODE = 0;
    protected static final int RELOAD_ACTIVITY = 990;
    private @Nullable Context context;
    private @NonNull List<Activity> activities = new ArrayList<>();
    private @Nullable FragmentActivity fragmentActivity;
    private @Nullable ActivityListAdapter adapter;
    private @Nullable String idGroup;
    private @Nullable Group thisGroup;
    private @Nullable RecyclerView activitiesContainer;
    private @Nullable FloatingActionButton newActivityButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @NonNull View view = inflater.inflate(R.layout.fragment_group_activities, container, false);
        initFragment();
        bindLayoutObjects(view);
        setContainer();
        bindThisGroupAndActivities();

        Objects.requireNonNull(newActivityButton).setOnClickListener(v -> {
            @NonNull Intent i = new Intent(context, ActivityCreation.class);
            i.putExtra("ID", idGroup);
            startActivity(i);
        });
        return view;
    }

    /**
     * Set and organize the container (and adapter) for group activities.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void setContainer() {
        Objects.requireNonNull(activitiesContainer).setLayoutManager(new LinearLayoutManager(context));
        adapter = new ActivityListAdapter(activities);
        activitiesContainer.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    /**
     * Bind all layout objects contained in GroupActivities.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects(@NonNull View view) {
        activitiesContainer = view.findViewById(R.id.group_activities_list);
        newActivityButton = view.findViewById(R.id.group_add_activity);
    }

    /**
     * Find context, fragment activity and group of selected activity.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void initFragment() {
        context = requireContext();
        fragmentActivity = requireActivity();
        idGroup = fragmentActivity.getIntent().getStringExtra("ID");
    }

    /**
     * Start the ActivityDetails activity to see all group activity details and eventually join them.
     *
     * @author Daniele Dotto
     * @see "{@link it.unive.cybertech.groups.activities.ActivityDetails}"
     * @since 1.1
     */
    @Override
    public void onItemClick(View view, int position) {
        @NonNull Activity selected = activities.get(position);
        @NonNull Intent i = new Intent(fragmentActivity, ActivityDetails.class);
        i.putExtra("ID", idGroup);
        i.putExtra("ID_GroupActivity", selected.getId());
        startActivityForResult(i, ACTIVITY_DETAILS_CODE);
    }

    /**
     * Behaviour according to codes sent by other fragments or activities
     *
     * @author Daniele Dotto
     * @see "{@link it.unive.cybertech.groups.activities.ActivityDetails}"
     * @since 1.1
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTIVITY_DETAILS_CODE) {
            if(resultCode == RELOAD_ACTIVITY) {
                if (fragmentActivity != null) {
                    fragmentActivity.recreate();
                }
            }
        }
    }

    /**
     * Find group and all group activities by ID group.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    @SuppressLint("NotifyDataSetChanged")
    private void bindThisGroupAndActivities() {
        executeAsync(() -> obtainGroupById(idGroup), new TaskResult<Group>() {
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
        executeAsync(() -> getThisGroup().getMaterializedActivities(), new TaskResult<List<Activity>>() {
            @Override
            public void onComplete(@NonNull List<Activity> result) {
                activities = result;
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
        getAdapter().setItems(activities);
        getAdapter().notifyDataSetChanged();
    }

    /**
     * Return the current group only if that is not null.
     *
     * @return "{@link #thisGroup}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull Group getThisGroup() {
        return Objects.requireNonNull(thisGroup);
    }

    /**
     * Return the group activity adapter only if that is not null.
     *
     * @return "{@link #adapter}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull ActivityListAdapter getAdapter() {
        return Objects.requireNonNull(adapter);
    }

}