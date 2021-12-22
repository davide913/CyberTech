package it.unive.cybertech.groups;

import static android.content.ContentValues.TAG;
import static it.unive.cybertech.database.Groups.Group.getAllGroups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;

public class HomePage extends Fragment implements Utils.ItemClickListener {
    private @NonNull List<Group> groups = new ArrayList<>();
    private @Nullable FragmentActivity fragmentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @NonNull View view = inflater.inflate(R.layout.fragment_home_page_groups, container, false);
        @NonNull Context context = view.getContext();
        fragmentActivity = getActivity();

        @NonNull FloatingActionButton newGroupButton = view.findViewById(R.id.add_group);
        @NonNull RecyclerView groupsContainer = view.findViewById(R.id.groups_list);
        groupsContainer.setLayoutManager(new LinearLayoutManager(context));
        @NonNull GroupListAdapter adapter = new GroupListAdapter(groups);
        groupsContainer.setAdapter(adapter);
        adapter.setClickListener(this);

        @NonNull Thread t = new Thread(() -> {
            try {
                groups = getAllGroups();
                Log.d("Size", " " + groups.size());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
            adapter.setItems(groups);
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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