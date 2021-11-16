package it.unive.cybertech.noleggio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.unive.cybertech.R;

public class HomePage extends Fragment implements ShowcaseAdapter.ItemClickListener {

    private ArrayList<String> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_noleggio, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.showcase_list);
        FloatingActionButton add = view.findViewById(R.id.showcase_add);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            items.add("prova" + i);
        ShowcaseAdapter adapter = new ShowcaseAdapter(items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        add.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProductForRent.class));
        });
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(getActivity(), ProductDetails.class);
        i.putExtra("ID", items.get(position));
        startActivity(i);
    }
}