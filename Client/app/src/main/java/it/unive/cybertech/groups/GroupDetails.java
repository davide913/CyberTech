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
        TabLayout tabLayout = findViewById(R.id.group_tabs);
        ViewPager viewPager = findViewById(R.id.group_viewpager);
        tabLayout.setupWithViewPager(viewPager);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new GroupInfo(), getString(R.string.information));
        adapter.addFragment(new GroupActivities(), getString(R.string.activity));
        viewPager.setAdapter(adapter);
    }

    private static class Adapter extends FragmentPagerAdapter {

        private final List<Pair<String, Fragment>> mFragmentList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position).second;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(new Pair<>(title, fragment));
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return  mFragmentList.get(position).first;
        }
    }
}