package it.unive.cybertech.groups;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.unive.cybertech.R;
import it.unive.cybertech.utils.Utils;

public class HomePage extends Fragment implements Utils.ItemClickListener {
    private ArrayList<String> groups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page_groups, container, false);
        RecyclerView lst = view.findViewById(R.id.groups_list);
        FloatingActionButton add = view.findViewById(R.id.add_group);
        lst.setLayoutManager(new LinearLayoutManager(getContext()));
        groups = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            groups.add("" + i);
        GroupListAdapter adapter = new GroupListAdapter(groups);
        lst.setAdapter(adapter);
        adapter.setClickListener(this);
        add.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreateGroup.class)));
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(getActivity(), GroupDetails.class);
        i.putExtra("ID", groups.get(position));
        startActivity(i);
    }
}