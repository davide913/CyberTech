package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.CachedUser.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.utils.Utils;

public class MyRentMaterialsFragment extends Fragment implements Utils.ItemClickListener {

    private ArrayList<Material> items;
    private RentMaterialAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rent_materials, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.my_rent_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new ArrayList<>();
        adapter = new RentMaterialAdapter(items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        initList();
        return view;
    }

    private void initList() {
        super.onStart();
        //TODO get posizione
        Thread t = new Thread(() -> {
            try {
                items = user.getMaterializedUserMaterials();
                Log.d("noleggio.MyRentMaterialsFragment", "Size: " + items.size());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    public void onItemClick(View view, int position) {

    }
}