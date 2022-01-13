package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.CachedUser.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.utils.Utils;

/**
 * This activity shows the expired rent and lending for the user
 * Rent refers to the material that the user offers to others, while rented means that material that the user borrowed from others
 *
 * It returns some data in order to refresh the list in case of complete rent
 *
 * @author Mattia Musone
 */
public class ExpiredRents extends AppCompatActivity implements Utils.ItemClickListener {

    public static final String ID = "ExpiredRents";
    static final int CONFIRM_END_RENT = 0;
    static final int CONFIRM_END_LENDING = 1;
    ///Declaring the adapter for the two kind of rent
    private RentedMaterialsAdapter rentMaterialsAdapter, rentedMaterialsAdapter;
    private List<LendingInProgress> rentMaterials, rentedMaterials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expired_rents);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.deadlines);
        RecyclerView rent = findViewById(R.id.expired_rent_list),
                rented = findViewById(R.id.expired_rented_list);
        rentMaterials = new ArrayList<>();
        rentedMaterials = new ArrayList<>();
        rentMaterialsAdapter = new RentedMaterialsAdapter(rentMaterials, RentMaterialAdapter.ID);
        rentedMaterialsAdapter = new RentedMaterialsAdapter(rentedMaterials, RentedMaterialsAdapter.ID);
        rentedMaterialsAdapter.setClickListener(this);
        rentMaterialsAdapter.setClickListener(this);
        rent.setLayoutManager(new GridLayoutManager(this, 2));
        rented.setLayoutManager(new GridLayoutManager(this, 2));
        rent.setAdapter(rentMaterialsAdapter);
        rented.setAdapter(rentedMaterialsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ///Binding data to the rented list and refreshing the relative adapter
        Utils.executeAsync(() -> user.obtainExpiredLending(), new Utils.TaskResult<List<LendingInProgress>>() {
            @Override
            public void onComplete(List<LendingInProgress> result) {
                rentedMaterials = result;
                rentedMaterialsAdapter.setItems(rentedMaterials);
                rentedMaterialsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        ///Binding data to the rent list and refreshing the relative adapter
        Utils.executeAsync(() -> user.obtainMyMaterialsExpiredLending(), new Utils.TaskResult<List<LendingInProgress>>() {
            @Override
            public void onComplete(List<LendingInProgress> result) {
                rentMaterials = result;
                rentMaterialsAdapter.setItems(rentMaterials);
                rentMaterialsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * When an item of both list is clicked, we start the activity ProductDetails - "{@link it.unive.cybertech.noleggio.ProductDetails}"
     * passing to it some information like:
     * - Type: from which adapter the request come from
     * - Position: the position of the item in the relative list
     */
    @Override
    public void onItemClick(View view, int position) {
        String tag = view.getTag().toString();
        Intent i = new Intent(this, ProductDetails.class);
        i.putExtra("Type", tag);
        i.putExtra("Position", position);
        if (tag.equals(RentedMaterialsAdapter.ID)) {
            i.putExtra("ID", rentedMaterials.get(position).getId());
            startActivityForResult(i, CONFIRM_END_LENDING);
        } else {
            i.putExtra("ID", rentMaterials.get(position).getId());
            startActivityForResult(i, CONFIRM_END_RENT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ///If the rent is concluded, then remove it from the expired list
        if (requestCode == CONFIRM_END_RENT) {
            if (resultCode == ProductDetails.RENT_TERMINATED) {
                int pos = data.getIntExtra("Position", -1);
                if (pos >= 0)
                    rentMaterialsAdapter.removeAt(pos);
            }
            ///If the rented (lending) is concluded, then remove it from the expired list
        } else if (requestCode == CONFIRM_END_LENDING) {
            if (resultCode == ProductDetails.RENT_TERMINATED) {
                int pos = data.getIntExtra("Position", -1);
                if (pos >= 0)
                    rentedMaterialsAdapter.removeAt(pos);
            }
        }
    }
}