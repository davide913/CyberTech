package it.unive.cybertech.noleggio;

import static it.unive.cybertech.noleggio.HomePage.RENT_CODE;
import static it.unive.cybertech.utils.CachedUser.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.utils.Utils;

/**
 * This class shows the material that a user offers to others.
 *
 * @author Mattia Musone
 */
public class MyRentMaterialsFragment extends Fragment implements Utils.ItemClickListener {

    public static final String ID = "MyRentMaterialsFragment";
    ///The list of items owned by the user
    private List<Material> items;
    private RentMaterialAdapter adapter;
    private ProgressBar loader;
    private RecyclerView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rent_materials, container, false);
        list = view.findViewById(R.id.my_rent_list);
        loader = view.findViewById(R.id.my_rent_loader);
        list.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new ArrayList<>();
        adapter = new RentMaterialAdapter(items);
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
     * This function initialize the list getting data from the database asynchronous
     */
    private void initList() {
        super.onStart();
        Utils.executeAsync(() -> user.obtainMaterializedUserMaterials(), new Utils.TaskResult<List<Material>>() {
            @Override
            public void onComplete(List<Material> result) {
                Log.d(ID, "Size: " + result.size());
                ///Bind data to local list and update the adapter view
                items = result;
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
                //Hide the loader
                loader.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RENT_CODE)
            //If a material has been deleted, remove it from the list
            if (resultCode == ProductDetails.SUCCESS || resultCode == ProductDetails.RENT_DELETE) {
                int pos = data.getIntExtra("Position", -1);
                if (pos >= 0) {
                    adapter.removeAt(pos);
                    items.remove(pos);
                }
            }
    }

    /**
     * When an item is clicked, it opens the activity that shows the details.
     * Some data are passed in order to obtain information on the activity result
     *
     * @param view     the view clicked
     * @param position the item position in the list
     */
    public void onItemClick(View view, int position) {
        Intent i = new Intent(getActivity(), ProductDetails.class);
        i.putExtra("ID", items.get(position).getId());
        i.putExtra("Position", position);
        i.putExtra("Type", ID);
        startActivityForResult(i, RENT_CODE);
    }

    /**
     * This function add a material to the list of rent
     *
     * @param material The material to add in the list
     */
    public void addMaterialToList(@NonNull Material material) {
        if (adapter != null)
            adapter.add(material);
        items.add(material);
    }
}