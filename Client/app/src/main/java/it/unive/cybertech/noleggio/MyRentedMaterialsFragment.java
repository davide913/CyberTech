package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.CachedUser.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.utils.Utils;

/**
 * This fragment shows the material that user has borrowed from others (also referred as lending)
 *
 * @author Mattia Musone
 */
public class MyRentedMaterialsFragment extends Fragment implements Utils.ItemClickListener {

    public static final String ID = "MyRentedMaterialsFragment";
    private List<LendingInProgress> items;
    private RentedMaterialsAdapter adapter;
    private ProgressBar loader;
    private RecyclerView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rented_materials, container, false);
        list = view.findViewById(R.id.my_rented_list);
        loader = view.findViewById(R.id.my_rented_loader);
        list.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new ArrayList<>();
        adapter = new RentedMaterialsAdapter(items);
        adapter.setClickListener(this);
        list.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initList();
    }

    /**
     * This function get data from the database and load it into the list asynchronous
     */
    private void initList() {
        super.onStart();
        Utils.executeAsync(() -> user.obtainMaterializedLendingInProgress(), new Utils.TaskResult<List<LendingInProgress>>() {
            @Override
            public void onComplete(List<LendingInProgress> result) {
                Log.d(ID, "Size: " + result.size());
                ///Bind data to local list and update the adapter view
                items = result;
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
                loader.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This function is called when an item is clicked.
     * It opens the ProductDetails - "{@link it.unive.cybertech.noleggio.ProductDetails}" in order to show item data
     */
    public void onItemClick(View view, int position) {
        Intent i = new Intent(getActivity(), ProductDetails.class);
        i.putExtra("ID", items.get(position).getId());
        i.putExtra("Position", position);
        i.putExtra("Type", ID);
        startActivity(i);
    }

    /**
     * This function is used to update the lending list adding a new lending by id and update the adapter
     */
    public void addLendingById(@NonNull String id) {
        Utils.executeAsync(() -> LendingInProgress.obtainLendingInProgressById(id), new Utils.TaskResult<LendingInProgress>() {
            @Override
            public void onComplete(LendingInProgress result) {
                adapter.add(result);
                //items.add(result);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}