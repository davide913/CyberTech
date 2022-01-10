package it.unive.cybertech.noleggio;

import static it.unive.cybertech.noleggio.HomePage.NEW_MATERIAL;
import static it.unive.cybertech.noleggio.HomePage.RENT_CODE;
import static it.unive.cybertech.utils.CachedUser.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.utils.Utils;

public class ShowcaseFragment extends Fragment implements Utils.ItemClickListener {

    public static final String ID = "ShowcaseFragment";
    private static final int PERMISSIONS_FINE_LOCATION = 5;
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
        try {
            Utils.getLocation(getActivity(), new Utils.TaskResult<Utils.Location>() {
                @Override
                public void onComplete(Utils.Location result) {
                    Utils.executeAsync(() -> Material.obtainRentableMaterials(result.latitude, result.longitude, 100, user.getId()), new Utils.TaskResult<List<Material>>() {
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
                            loader.setVisibility(View.GONE);
                            return null;
                        }
                    });
                }

                @Override
                public OnFailureListener onError(Exception e) {
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                    return null;
                }
            });

        } catch (Utils.PermissionDeniedException e) {
            e.printStackTrace();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
                Utils.executeAsync(() -> Material.obtainMaterialById(id), new Utils.TaskResult<Material>() {
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
                    public OnFailureListener onError(Exception e) {
                        return null;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            new Utils.Dialog(getContext())
                    .hideCancelButton()
                    .show(getString(R.string.position_required), getString(R.string.position_required_description));
            loader.setVisibility(View.GONE);
        } else {
            initList();
        }
    }
}