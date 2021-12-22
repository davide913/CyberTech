package it.unive.cybertech.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;

public class HomePage extends Fragment implements Utils.ItemClickListener {
    private @Nullable List<Group> groups;
    private @Nullable FragmentActivity fragmentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @NonNull View view = inflater.inflate(R.layout.fragment_home_page_groups, container, false);
        @NonNull Context context = view.getContext();
        fragmentActivity = getActivity();


        groups = new ArrayList<>(); // todo da cancellare quando esisterà getAllGroups()
        groups.add(new Group());    // todo da cancellare quando esisterà getAllGroups()
        //groups = getAllGroups(double latitude, double longitude);   // todo DB factory function
        @NonNull FloatingActionButton newGroupButton = view.findViewById(R.id.add_group);
        @NonNull RecyclerView groupsContainer = view.findViewById(R.id.groups_list);
        groupsContainer.setLayoutManager(new LinearLayoutManager(context));
        @NonNull GroupListAdapter adapter = new GroupListAdapter(groups);
        groupsContainer.setAdapter(adapter);
        adapter.setClickListener(this);

        newGroupButton.setOnClickListener(v -> startActivity(new Intent(fragmentActivity, GroupCreation.class)));
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        @NonNull Group selected = Objects.requireNonNull(groups).get(position);
        @NonNull Intent i = new Intent(fragmentActivity, GroupHomePage.class);
        i.putExtra("ID", selected.getId());
        startActivity(i);
    }


}