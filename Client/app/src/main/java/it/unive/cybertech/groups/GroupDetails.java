package it.unive.cybertech.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.EditPassword;
import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;

public class GroupDetails extends AppCompatActivity {
    private final @NonNull Context context = this;
    private Group thisGroup;
    private String idGroup;

    private TextView nameGroup;
    private TextView descriptionGroup;
    private TextView nUsers;

    private FloatingActionButton newActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        initTabs();
        initActionBar();

        bindThisGroup();

        // INFORMAZIONI
        nameGroup = findViewById(R.id.group_information_tab_name);
        descriptionGroup = findViewById(R.id.group_information_tab_description);
        nUsers = findViewById(R.id.group_information_tab_nUsers);
        bindInfoGroupTextViewsValues();

        // ATTIVITA'
        newActivity = findViewById(R.id.group_activity_tab_newActivity);
        newActivity.setOnClickListener(v -> {
            @NonNull Intent i = new Intent(context, ActivityCreation.class);
            i.putExtra("ID", idGroup);
            startActivity(i);
        });

    }

    private void bindInfoGroupTextViewsValues() {
        nameGroup.setText(thisGroup.getName());
        descriptionGroup.setText(thisGroup.getDescription());
        nUsers.setText(thisGroup.getMembers().size());
    }

    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(idGroup);
    }

    private void bindThisGroup() {
        Thread t = new Thread(() -> {
            try {
                thisGroup = Group.getGroupById(getIntent().getStringExtra("ID"));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            idGroup = thisGroup.getId();
            if(idGroup == null || idGroup.length() == 0)
                finish();
        });
        t.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTabs(){
        TabLayout tabLayout = findViewById(R.id.groups_tabs);
        ViewPager viewPager = findViewById(R.id.groups_viewpager);
        tabLayout.setupWithViewPager(viewPager);
        Utils.FragmentAdapter adapter = new Utils.FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupInfo(), getString(R.string.information));
        adapter.addFragment(new GroupActivities(), getString(R.string.activity));
        viewPager.setAdapter(adapter);
    }
}