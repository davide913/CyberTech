package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.CachedUser.user;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.utils.Utils;

public class ProductDetails extends AppCompatActivity {

    private Material material;
    private String id;
    private ImageView photo;
    private TextView title, description, date;
    static final int RENT_SUCCESS = 1;
    static final int RENT_FAIL = 0;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_noleggio);
        Intent i = getIntent();
        id = i.getStringExtra("ID");
        pos = i.getIntExtra("Position", -1);
        if (id == null || id.isEmpty())
            finish();
        photo = findViewById(R.id.product_image_details_showcase);
        title = findViewById(R.id.title_details_showcase);
        description = findViewById(R.id.description_details_showcase);
        date = findViewById(R.id.expiring_date_details_showcase);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread t = new Thread(() -> {
            try {
                material = Material.getMaterialById(id);
            } catch (ExecutionException | InterruptedException e) {
                finish();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Dettaglio: " + material.getTitle());
        title.setText(material.getTitle());
        description.setText(material.getDescription());
        date.setText(new SimpleDateFormat("dd/MM/yyyy").format(material.getExpiryDate().toDate()));
        if (material.getPhoto() != null) {
            byte[] arr = Base64.decode(material.getPhoto(), Base64.DEFAULT);
            photo.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
        }
        FloatingActionButton confirm = findViewById(R.id.confirm_rent_showcase);
        confirm.setOnClickListener(view -> {
            if (user.getLendingPoint() < 0)
                new Utils.Dialog(this)
                        .hideCancelButton()
                        .show(getString(R.string.unreliable), getString(R.string.unreliable_description));
            else
                new Utils.Dialog(this)
                        .setCallback(new Utils.DialogResult() {
                            @Override
                            public void onSuccess() {
                                AtomicBoolean done = new AtomicBoolean(false);
                                Thread t = new Thread(() -> {
                                    try {
                                        material.updateRenter(user);
                                        LendingInProgress.createLendingInProgress(material, material.getExpiryDate().toDate());
                                    done.set(true);
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                                t.start();
                                try {
                                    t.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent res = new Intent();
                                res.putExtra("Position", pos);
                                setResult(done.get() ? RENT_SUCCESS : RENT_FAIL, res);
                                finish();
                            }

                            @Override
                            public void onCancel() {

                            }
                        })
                        .show(getString(R.string.rent_disclosure), getString(R.string.rent_disclosure_description));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}