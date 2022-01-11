package it.unive.cybertech.groups;

import static it.unive.cybertech.utils.Utils.executeAsync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;
import it.unive.cybertech.utils.Utils.TaskResult;

/**
 * HomePage is a main fragment that allow user to view all Families Share (plugin) groups.
 * Users can create new groups:
 *
 * @author Daniele Dotto
 * @see "{@link it.unive.cybertech.groups.GroupCreation}"
 * @since 1.1
 */
public class HomePage extends Fragment implements Utils.ItemClickListener {
    private static final int GROUP_INFO_CODE = 9;
    protected static final int RELOAD_GROUP = 991;
    private @Nullable
    Context context;
    private @NonNull
    List<Group> groups = new ArrayList<>();
    private @Nullable
    FragmentActivity fragmentActivity;
    private @Nullable
    RecyclerView groupsContainer;
    private @Nullable
    FloatingActionButton newGroupButton;
    private @Nullable
    GroupListAdapter adapter;
    private ProgressBar loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @NonNull final View view = inflater.inflate(R.layout.fragment_home_page_groups, container, false);
        initFragment();
        bindLayoutObjects(view);
        setContainer();

        findFSGroups();

        Objects.requireNonNull(newGroupButton).setOnClickListener(v -> startActivity(new Intent(fragmentActivity, GroupCreation.class)));
        return view;
    }

    /**
     * Set and organize the container (and adapter) for groups.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void setContainer() {
        Objects.requireNonNull(groupsContainer).setLayoutManager(new LinearLayoutManager(context));
        adapter = new GroupListAdapter(groups);
        groupsContainer.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    /**
     * Bind all layout objects contained in HomePage.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects(@NonNull final View view) {
        groupsContainer = view.findViewById(R.id.groups_list);
        newGroupButton = view.findViewById(R.id.add_group);
        loader = view.findViewById(R.id.groups_home_progress);
    }

    /**
     * Find context and fragment activity.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void initFragment() {
        context = requireContext();
        fragmentActivity = requireActivity();
    }

    /**
     * Initialize groups field with a List<Group>.
     * The list is composed by all Families Share (plugin) groups.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    @SuppressLint("NotifyDataSetChanged")
    private void findFSGroups() {
        executeAsync(Group::obtainAllGroups, new TaskResult<List<Group>>() {
            @Override
            public void onComplete(List<Group> result) {
                groups = result;
                getAdapter().setItems(groups);
                getAdapter().notifyDataSetChanged();
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                try {
                    throw new Exception(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * Start the GroupHomePage activity to see all group details and group activities.
     *
     * @author Daniele Dotto
     * @see "{@link it.unive.cybertech.groups.GroupHomePage}"
     * @since 1.1
     */
    @Override
    public void onItemClick(@NonNull final View view, int position) {
        @NonNull Group selected = getGroups().get(position);
        @NonNull Intent i = new Intent(fragmentActivity, GroupHomePage.class);
        i.putExtra("ID", selected.getId());
        startActivityForResult(i, GROUP_INFO_CODE);
    }


    /**
     * Behaviour according to codes sent by other fragments or activities
     *
     * @author Daniele Dotto
     * @see "{@link it.unive.cybertech.groups.GroupInfo}"
     * @since 1.1
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GROUP_INFO_CODE) {
            if (resultCode == RELOAD_GROUP) {
                if (fragmentActivity != null) {
                    fragmentActivity.recreate();
                }
            }
        }
    }

    /**
     * Return the entire group list only if that is not null.
     *
     * @return "{@link #groups}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    List<Group> getGroups() {
        return Objects.requireNonNull(groups);
    }

    /**
     * Return the group list adapter only if that is not null.
     *
     * @return "{@link #adapter}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    GroupListAdapter getAdapter() {
        return Objects.requireNonNull(adapter);
    }


}