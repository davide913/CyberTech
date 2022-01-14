package it.unive.cybertech.groups;

import static it.unive.cybertech.utils.Utils.executeAsync;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.groups.activities.GroupActivities;
import it.unive.cybertech.utils.Utils;
import it.unive.cybertech.utils.Utils.TaskResult;

/**
 * The main activity called by fragment "{@link it.unive.cybertech.groups.HomePage}".
 * It supplies the selected group.
 * It is splitted by two fragments:
 *
 * @author Daniele Dotto
 * @see "{@link it.unive.cybertech.groups.GroupInfo}"
 * @see "{@link it.unive.cybertech.groups.activities.GroupActivities}"
 * @since 1.1
 */
public class GroupHomePage extends AppCompatActivity {
    private @Nullable
    Group thisGroup;
    private ProgressBar loader;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home_page);
        loader = findViewById(R.id.group_home_loader);
        bindThisGroup();
    }

    /**
     * Find and bind the layout tab (and viewholder) and add the activity fragments.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void initTabs() {
        @NonNull TabLayout tabLayout = findViewById(R.id.groups_tabs);
        @NonNull ViewPager viewPager = findViewById(R.id.group_infoViewPager);
        tabLayout.setupWithViewPager(viewPager);
        @NonNull Utils.FragmentAdapter adapter = new Utils.FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupInfo(), getString(R.string.information), "GroupInfo");
        adapter.addFragment(new GroupActivities(), getString(R.string.activity), "GroupActivities");
        viewPager.setAdapter(adapter);
    }

    /**
     * Bind the selected group with group present in DB using the ID.
     *
     * @author Daniele Dotto
     * @see "{@link #thisGroup}"
     * @since 1.1
     */
    private void bindThisGroup() {
        executeAsync(() -> Group.obtainGroupById(getIntent().getStringExtra("ID")), new TaskResult<Group>() {

            @Override
            public void onComplete(@NonNull Group result) {
                thisGroup = result;
                loader.setVisibility(View.GONE);
                initActionBar();
                initTabs();
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
     * Initialize the action bar values.
     */
    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getThisGroup().getName());
    }

    /**
     * Manage the 'back button'.
     *
     * @param item The 'back button'
     * @return true when the current activity is finished
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
     * Check if "{@link #thisGroup}" is not null and return it
     *
     * @return the selected group
     */
    public @NonNull
    Group getThisGroup() {
        return Objects.requireNonNull(thisGroup);
    }
}