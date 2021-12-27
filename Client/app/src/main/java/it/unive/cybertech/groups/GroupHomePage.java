package it.unive.cybertech.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;

public class GroupHomePage extends AppCompatActivity {
    private Group thisGroup;
    private String idGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home_page);
        initTabs();
        initActionBar();
        bindThisGroup();
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

    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(idGroup);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Group getThisGroup() {
        return thisGroup;
    }
}