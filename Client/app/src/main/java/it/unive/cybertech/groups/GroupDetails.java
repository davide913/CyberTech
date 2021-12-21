package it.unive.cybertech.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.utils.Utils;

public class GroupDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        initTabs();
        String id = getIntent().getStringExtra("ID");
        if(id == null || id.length() == 0)
            finish();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(id);
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