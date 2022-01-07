package it.unive.cybertech.gestione_covid;

import static it.unive.cybertech.utils.CachedUser.user;

import androidx.fragment.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.gestione_covid.adapters.CustomSignReceivedAdapter;
import it.unive.cybertech.utils.CachedUser;

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

    private void initViews(View v) throws ExecutionException, InterruptedException {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Activity> activityList = user.obtainPositiveActivities();
                    Boolean var = false;
                    ImageView imageView = v.findViewById(R.id.imageView_PosReported);
                    TextView textView = v.findViewById(R.id.TextView_PosReported);
                    TextView textView1 = v.findViewById(R.id.textView_UltimeSegnalazioni);
                    ListView listView = v.findViewById(R.id.ListView_signReported);

                    if(!activityList.isEmpty())
                        var = true;

                    if(var){
                        imageView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);

                        ArrayAdapter<Activity> adapter;

                        adapter = new CustomSignReceivedAdapter(getContext(), 0, activityList);

                        listView.setAdapter(adapter);
                    }
                    else{
                        imageView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        textView1.setVisibility(View.INVISIBLE);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
        t.join();

    }


}