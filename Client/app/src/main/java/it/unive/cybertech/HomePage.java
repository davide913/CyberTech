package it.unive.cybertech;

import static it.unive.cybertech.utils.CachedUser.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;

/**
 * This class is a Dashboard to summarize the user status
 *
 * @author Mattia Musone
 * */
public class HomePage extends Fragment {

    private TextView covidTxt, assistanceTxt, rentTxt, groupsTxt, username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        CardView covid = view.findViewById(R.id.home_page_covid_card),
                assistance = view.findViewById(R.id.home_page_assistance_card),
                rent = view.findViewById(R.id.home_page_rent_card),
                groups = view.findViewById(R.id.home_page_groups_card);
        covidTxt = view.findViewById(R.id.home_page_user_status);
        assistanceTxt = view.findViewById(R.id.home_page_user_helping);
        rentTxt = view.findViewById(R.id.home_page_user_rent);
        groupsTxt = view.findViewById(R.id.home_page_user_groups);
        username = view.findViewById(R.id.home_page_username);
        covid.setOnClickListener(v -> {
            getMainActivity().openSection(R.id.nav_menu_covid);
        });
        assistance.setOnClickListener(v -> {
            getMainActivity().openSection(R.id.nav_menu_quarantine_assistance);
        });
        rent.setOnClickListener(v -> {
            getMainActivity().openSection(R.id.nav_menu_showcase);
        });
        groups.setOnClickListener(v -> {
            getMainActivity().openSection(R.id.nav_menu_groups);
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user.getPositiveSince() == null) {
            covidTxt.setText(R.string.negative);
            covidTxt.setTextColor(getResources().getColor(R.color.light_green_fs));
        } else {
            covidTxt.setText(R.string.positive);
            covidTxt.setTextColor(getResources().getColor(R.color.red_fs));
        }
        username.setText(user.getName() + " " + user.getSurname());
        Utils.executeAsync(() -> user.getLendingInProgress(), new Utils.TaskResult<List<DocumentReference>>() {
            @Override
            public void onComplete(List<DocumentReference> result) {
                if (result != null)
                    rentTxt.setText(String.valueOf(result.size()));
                else
                    rentTxt.setText("-");
            }

            @Override
            public void onError(Exception e) {

            }
        });
        Utils.executeAsync(() -> user.getQuarantineAssistance(), new Utils.TaskResult<List<DocumentReference>>() {
            @Override
            public void onComplete(List<DocumentReference> result) {
                if (result != null)
                    assistanceTxt.setText(String.valueOf(result.size()));
                else
                    assistanceTxt.setText("-");
            }

            @Override
            public void onError(Exception e) {

            }
        });
        Utils.executeAsync(() -> user.obtainGroups(), new Utils.TaskResult<List<Group>>() {
            @Override
            public void onComplete(List<Group> result) {
                if (result != null)
                    groupsTxt.setText(String.valueOf(result.size()));
                else
                    groupsTxt.setText("-");
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * Gets the activity associated to this fragment
     * */
    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}