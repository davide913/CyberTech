package it.unive.cybertech.gestione_covid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import it.unive.cybertech.R;
import it.unive.cybertech.utils.Utils;

/**
 * HomePage is the Fragment that initializes the
 * ViewPager and creates the various Fragments displayed.
 *
 * @author Enrico De Zorzi
 * @since 1.0
 */
public class HomePage extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_covid, container, false);
        initViews(view);
        return view;
    }

    /**
     * setupViewPager initializes (with the adapter) the ViewPager and creates the Fragments.
     *
     * @author Enrico De Zorzi
     * @since 1.0
     */
    private void setupViewPager(ViewPager viewPager) {
        Utils.FragmentAdapter adapter = new Utils.FragmentAdapter(getParentFragmentManager());
        adapter.addFragment(new ManifestPositivityFragment(), getString(R.string.set_positivity), "ManifestPositivityFragment"); //Creation of the first fragments
        adapter.addFragment(new PosReportedFragment(), getString(R.string.received_reports), "PosReportedFragment"); //Creation of the second fragments
        viewPager.setAdapter(adapter);
    }

    private void initViews(View view) {
        TabLayout tabLayout = view.findViewById(R.id.group_tabs);
        ViewPager viewPager = view.findViewById(R.id.groups_viewpager);
        Toolbar toolbar_covid_homepage = view.findViewById(R.id.main_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar_covid_homepage);
        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);
    }
}
