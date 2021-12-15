package it.unive.cybertech.noleggio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.utils.Utils;

public class HomePage extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_noleggio, container, false);

        initTabs(view);
        return view;
    }

    private void initTabs(View view){
        TabLayout tabLayout = view.findViewById(R.id.showcase_tabs);
        ViewPager viewPager = view.findViewById(R.id.showcase_viewpager);
        tabLayout.setupWithViewPager(viewPager);
        Utils.FragmentAdapter adapter = new Utils.FragmentAdapter(getParentFragmentManager());
        adapter.addFragment(new ShowcaseFragment(), getString(R.string.showcase));
        adapter.addFragment(new MyRentedMaterialsFragment(), getString(R.string.rented_materials));
        adapter.addFragment(new MyRentMaterialsFragment(), getString(R.string.rent_materials));
        viewPager.setAdapter(adapter);
    }
}