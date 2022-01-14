package it.unive.cybertech.gestione_covid;

import static it.unive.cybertech.utils.CachedUser.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.gestione_covid.adapters.CustomSignReceivedAdapter;
import it.unive.cybertech.utils.Utils;

/**
 * PosReportedFragment is the second fragment of the Covid-19 Section.
 * In this Fragment it is possible to view (if present) the positive reports received from other users.
 *
 * @author Enrico De Zorzi
 * @since 1.0
 */
public class PosReportedFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pos_reported, container, false);

        try {
            initViews(v);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return v;
    }

    /**
     * InitViews initializes the screen.
     * Set the correct values in the various fields.
     * Set the fields to Visible or Invisible based on the reports received
     *
     * @author Enrico De Zorzi
     * @since 1.0
     */
    private void initViews(View v) throws ExecutionException, InterruptedException {

        Utils.executeAsync(() -> user.obtainPositiveActivities(), new Utils.TaskResult<List<Activity>>() {
            @Override
            public void onComplete(List<Activity> result) {
                List<Activity> activityList = result; //activity list with positives
                Boolean var = false;
                ImageView imageView = v.findViewById(R.id.imageView_PosReported);
                TextView textView = v.findViewById(R.id.TextView_PosReported);
                TextView textView1 = v.findViewById(R.id.textView_UltimeSegnalazioni);
                ListView listView = v.findViewById(R.id.ListView_signReported);

                if (!activityList.isEmpty())
                    var = true;

                if (var) {
                    imageView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    textView1.setVisibility(View.VISIBLE);

                    ArrayAdapter<Activity> adapter;

                    adapter = new CustomSignReceivedAdapter(getContext(), 0, activityList); //creation of the ListView

                    listView.setAdapter(adapter);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    textView1.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


}