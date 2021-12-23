package it.unive.cybertech.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Group;

public class GroupActivities extends Fragment {

    public static final String ID = "GroupActivities";
    private Group thisGroup;
    private String idGroup;
    private @Nullable List<Activity> activities;
    private @Nullable FragmentActivity fragmentActivity;
    private FloatingActionButton newActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @NonNull View view = inflater.inflate(R.layout.fragment_group_activities, container, false);
        @NonNull Context context = view.getContext();
        fragmentActivity = getActivity();

        // todo manca lista delle activity del gruppo

        // ATTIVITA'
        newActivity = view.findViewById(R.id.group_add_activity);
        newActivity.setOnClickListener(v -> {
            @NonNull Intent i = new Intent(context, ActivityCreation.class);
            i.putExtra("ID", idGroup);
            startActivity(i);
        });





        return inflater.inflate(R.layout.fragment_group_activities, container, false);
    }


}