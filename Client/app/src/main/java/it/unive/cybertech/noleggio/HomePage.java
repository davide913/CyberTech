package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.CachedUser.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

public class HomePage extends Fragment implements Utils.ItemClickListener {

    private ArrayList<Material> items;
    private ShowcaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_noleggio, container, false);
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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //TODO get posizione
        Thread t = new Thread(() -> {
            try {
                items = User.getRentableMaterials(45, 12, 10000);
                Log.d("noleggio.HomePage", "Size: "+items.size());
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
        adapter.notifyDataSetChanged();
    }

    public void onItemClick(View view, int position) {
        Intent i = new Intent(getActivity(), ProductDetails.class);
        i.putExtra("ID", items.get(position).getId());
        startActivity(i);
    }
}