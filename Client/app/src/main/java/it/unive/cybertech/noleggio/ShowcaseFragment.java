package it.unive.cybertech.noleggio;

import static it.unive.cybertech.noleggio.HomePage.NEW_MATERIAL;
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
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.utils.Utils;

public class ShowcaseFragment extends Fragment implements Utils.ItemClickListener {

    public static final String ID = "ShowcaseFragment";
    private List<Material> items;
    private ShowcaseAdapter adapter;
    private ProgressBar loader;
    private RecyclerView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_showcase, container, false);
        list = view.findViewById(R.id.showcase_list);
        FloatingActionButton add = view.findViewById(R.id.showcase_add);
        list.setLayoutManager(new GridLayoutManager(getContext(), 2));
        items = new ArrayList<>();
        adapter = new ShowcaseAdapter(items);
        adapter.setClickListener(this);
        loader = view.findViewById(R.id.showcase_loader);
        list.setAdapter(adapter);
        add.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), AddProductForRent.class), NEW_MATERIAL);
        });
        initList();
        return view;
    }

    private void initList() {
        super.onStart();
        //TODO get posizione
        Utils.executeAsync(() -> Material.getRentableMaterials(45, 12, 10000, user.getId()), new Utils.TaskResult<List<Material>>() {
            @Override
            public void onComplete(List<Material> result) {
                Log.d(ID, "Size: " + result.size());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO gestire aggiunta materiale
        if (requestCode == RENT_CODE && resultCode == ProductDetails.SUCCESS) {
            int pos = data.getIntExtra("Position", -1);
            if (pos >= 0) {
                adapter.removeAt(pos);
                String idLending = data.getStringExtra("LendingID");
                if (idLending != null) {
                    HomePage h = (HomePage) getParentFragment();
                    if (h != null) {
                        MyRentedMaterialsFragment f = (MyRentedMaterialsFragment) h.getFragmentByID(MyRentedMaterialsFragment.ID);
                        if (f != null)
                            f.addLendingById(idLending);
                    }
                }
            }
        } else if (requestCode == NEW_MATERIAL && resultCode == ProductDetails.SUCCESS) {
            String id = data.getStringExtra("ID");
            if (id != null) {
                Utils.executeAsync(() -> Material.getMaterialById(id), new Utils.TaskResult<Material>() {
                    @Override
                    public void onComplete(Material result) {
                        adapter.add(result);
                        HomePage h = (HomePage) getParentFragment();
                        if (h != null) {
                            MyRentMaterialsFragment f = (MyRentMaterialsFragment) h.getFragmentByID(MyRentMaterialsFragment.ID);
                            if (f != null)
                                f.addMaterialToList(result);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }
    }

    public void onItemClick(View view, int position) {
        if (user.getLendingPoint() < 0) {
            HomePage h = (HomePage) getParentFragment();
            if (h != null)
                h.notifyNegativeLendingPoint();
        } else {
            Intent i = new Intent(getActivity(), ProductDetails.class);
            Material m = items.get(position);
            i.putExtra("ID", m.getId());
            i.putExtra("Position", position);
            i.putExtra("Type", m.getOwner().getId().equals(user.getId()) ? MyRentMaterialsFragment.ID : ID);
            startActivityForResult(i, RENT_CODE);
        }
    }
}