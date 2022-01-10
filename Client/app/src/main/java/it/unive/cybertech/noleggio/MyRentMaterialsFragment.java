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
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.utils.Utils;

public class MyRentMaterialsFragment extends Fragment implements Utils.ItemClickListener {

    public static final String ID = "MyRentMaterialsFragment";
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

    private void initList() {
        super.onStart();
        Utils.executeAsync(() -> user.obtainMaterializedUserMaterials(), new Utils.TaskResult<List<Material>>() {
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
            public OnFailureListener onError(Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RENT_CODE)
            if (resultCode == ProductDetails.SUCCESS || resultCode == ProductDetails.RENT_DELETE) {
                int pos = data.getIntExtra("Position", -1);
                if (pos >= 0) {
                    adapter.removeAt(pos);
                    //items.remove(pos);
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

    public void addMaterialToList(Material m) {
        if (adapter != null)
            adapter.add(m);
    }
}