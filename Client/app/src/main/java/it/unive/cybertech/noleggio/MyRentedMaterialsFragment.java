package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.CachedUser.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.utils.Utils;

public class MyRentedMaterialsFragment extends Fragment implements Utils.ItemClickListener {

    public static final String ID ="MyRentedMaterialsFragment";
    private List<LendingInProgress> items;
    private RentedMaterialsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rented_materials, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.my_rented_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new ArrayList<>();
        adapter = new RentedMaterialsAdapter(items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        initList();
        return view;
    }

    private void initList() {
        super.onStart();
        //TODO get posizione
        Utils.executeAsync(() -> user.getMaterializedLendingInProgress(), new Utils.TaskResult<List<LendingInProgress>>() {
            @Override
            public void onComplete(List<LendingInProgress> result) {
                Log.d(ID, "Size: " + result.size());
                items = result;
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onItemClick(View view, int position) {

    }

    public void addLendingById(String id){
        AtomicReference<LendingInProgress> l = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                l.set(LendingInProgress.getLendingInProgressById(id));
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
        adapter.add(l.get());
        //initList();
    }
}