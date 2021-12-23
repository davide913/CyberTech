package it.unive.cybertech.noleggio;

import static it.unive.cybertech.noleggio.HomePage.RENT_CODE;
import static it.unive.cybertech.utils.CachedUser.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.utils.Utils;

public class ShowcaseFragment extends Fragment implements Utils.ItemClickListener {

    public static final String ID = "ShowcaseFragment";
    private ArrayList<Material> items;
    private ShowcaseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_showcase, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.showcase_list);
        FloatingActionButton add = view.findViewById(R.id.showcase_add);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new ArrayList<>();
        /*for (int i = 0; i < 20; i++)
            items.add("prova" + i);*/
        adapter = new ShowcaseAdapter(items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        add.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProductForRent.class));
        });

        view.findViewById(R.id.test_showcase).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RentFeedback.class));
        });
        initList();
        return view;
    }

    private void initList() {
        super.onStart();
        //TODO get posizione
        Thread t = new Thread(() -> {
            try {
                items = Material.getRentableMaterials(45, 12, 10000, user.getId());
                Log.d("noleggio.HomePage", "Size: " + items.size());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RENT_CODE && resultCode == ProductDetails.RENT_SUCCESS) {
            int pos = data.getIntExtra("Position", -1);
            if (pos >= 0) {
                adapter.removeAt(pos);
                String idLending = data.getStringExtra("LendingID");
                if(idLending != null) {
                    HomePage h = (HomePage) getParentFragment();
                    if (h != null) {
                        MyRentedMaterialsFragment f = (MyRentedMaterialsFragment) h.getFragmentByID(MyRentedMaterialsFragment.ID);
                        if(f != null)
                            f.addLendingById(idLending);
                    }
                }
            }
        }
    }

    public void onItemClick(View view, int position) {
        Intent i = new Intent(getActivity(), ProductDetails.class);
        i.putExtra("ID", items.get(position).getId());
        i.putExtra("Position", position);
        i.putExtra("Type", ID);
        startActivityForResult(i, RENT_CODE);
    }
}