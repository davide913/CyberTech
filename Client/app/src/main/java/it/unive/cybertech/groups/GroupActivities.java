package it.unive.cybertech.groups;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.database.Groups.Group;

public class GroupActivities extends Fragment {
    private Group thisGroup;
    private String idGroup;
    private @Nullable List<Activity> activities;
    private @Nullable FragmentActivity fragmentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @NonNull View view = inflater.inflate(R.layout.fragment_group_activity, container, false);
        @NonNull Context context = view.getContext();
        fragmentActivity = getActivity();
        




        return inflater.inflate(R.layout.fragment_group_activity, container, false);
    }


}