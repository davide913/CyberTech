package it.unive.cybertech.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;

public class GroupInfo extends Fragment{


    private TextView nameGroup;
    private TextView descriptionGroup;
    private TextView nUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @NonNull View view = inflater.inflate(R.layout.fragment_group_information, container, false);


        // INFORMAZIONI
        nameGroup = view.findViewById(R.id.group_information_tab_name);
        descriptionGroup = view.findViewById(R.id.group_information_tab_description);
        nUsers = view.findViewById(R.id.group_information_tab_nUsers);

        @Nullable GroupHomePage activity = (GroupHomePage) getActivity();
        if (activity != null) {
            @NonNull Group thisGroup = activity.getThisGroup();
            nameGroup.setText(thisGroup.getName());
            descriptionGroup.setText(thisGroup.getDescription());
            nUsers.setText(String.valueOf(thisGroup.getMembers().size()));
        }

        return view;
    }
}