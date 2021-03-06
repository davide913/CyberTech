package it.unive.cybertech.noleggio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import it.unive.cybertech.R;
import it.unive.cybertech.utils.Utils;

/**
 * This activity is the main activity to manage the tab about the different menu
 * - Showcase "{@link it.unive.cybertech.noleggio.ShowcaseFragment}"
 * - MyRentedMaterials "{@link it.unive.cybertech.noleggio.MyRentedMaterialsFragment}"
 * - MyRentMaterials "{@link it.unive.cybertech.noleggio.MyRentMaterialsFragment}"
 *
 * @author Mattia Musone
 */
public class HomePage extends Fragment {

    public static final int RENT_CODE = 0, NEW_MATERIAL = 1;
    ///The fragment adapter to hold the views
    private Utils.FragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_noleggio, container, false);
        initTabs(view);
        return view;
    }

    /**
     * This method is used to load the views and initialize the adapter
     */
    private void initTabs(View view) {
        TabLayout tabLayout = view.findViewById(R.id.showcase_tabs);
        ViewPager viewPager = view.findViewById(R.id.showcase_viewpager);
        tabLayout.setupWithViewPager(viewPager);
        adapter = new Utils.FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new ShowcaseFragment(), getString(R.string.showcase), ShowcaseFragment.ID);
        adapter.addFragment(new MyRentedMaterialsFragment(), getString(R.string.rented_materials), MyRentedMaterialsFragment.ID);
        adapter.addFragment(new MyRentMaterialsFragment(), getString(R.string.rent_materials), MyRentMaterialsFragment.ID);
        viewPager.setAdapter(adapter);
    }

    /**
     * Look up in the fragment adapter and return, if any, the fragment requested
     *
     * @param id is the nonnull id of the fragment to search into the adapter
     * @return the fragment with the specified id or null if no mach
     */
    @Nullable
    public Fragment getFragmentByID(@NonNull String id) {
        return adapter.getFragmentById(id);
    }

    /**
     * Display a dialog warning about the unreliable user
     */
    public void notifyNegativeLendingPoint() {
        new Utils.Dialog(getContext())
                .hideCancelButton()
                .show(getString(R.string.unreliable), getString(R.string.unreliable_description));
    }
}