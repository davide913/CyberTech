package it.unive.cybertech.noleggio;

import static android.view.View.VISIBLE;
import static it.unive.cybertech.utils.CachedUser.user;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.utils.Utils;

public class ProductDetails extends AppCompatActivity {

    private Material material;
    private String id, from;
    private ImageView photo;
    private TextView title, description, date, renter, requestDate;
    private FloatingActionButton delete, confirm;
    private ConstraintLayout renterLayout, extensionLayout;
    private Button acceptExtension, rejectExtension;
    static final int RENT_DELETE = -1;
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
        from = i.getStringExtra("Type");
        if (id == null || id.isEmpty())
            finish();
        confirm = findViewById(R.id.confirm_rent_showcase);
        acceptExtension = findViewById(R.id.accept_extension_details);
        rejectExtension = findViewById(R.id.reject_extension_details);
        delete = findViewById(R.id.delete_rent_showcase);
        photo = findViewById(R.id.product_image_details_showcase);
        title = findViewById(R.id.title_details_showcase);
        description = findViewById(R.id.description_details_showcase);
        date = findViewById(R.id.expiring_date_details_showcase);
        renterLayout = findViewById(R.id.renter_layout_details);
        extensionLayout = findViewById(R.id.extension_layout_details);
        renter = findViewById(R.id.user_name_renter_details);
        requestDate = findViewById(R.id.expire_date_rent_details);

        if (from.equals(MyRentMaterialsFragment.ID))
            confirm.setVisibility(View.GONE);

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
        date.setText(Utils.formatDateToString(material.getExpiryDate().toDate()));
        if (material.getPhoto() != null) {
            byte[] arr = Base64.decode(material.getPhoto(), Base64.DEFAULT);
            photo.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
        }
        manageType();
    }

    private void manageType() {
        switch (from) {
            case MyRentMaterialsFragment.ID:
                if (material.getRenter() == null) {
                    delete.setVisibility(VISIBLE);
                    delete.setOnClickListener(v -> {
                        new Utils.Dialog(this)
                                .setCallback(new Utils.DialogResult() {
                                    @Override
                                    public void onSuccess() {
                                        //material.delete();
                                        Intent res = new Intent();
                                        res.putExtra("Position", pos);
                                        setResult(RENT_DELETE, res);
                                        finish();
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                })
                                .show("Operazione irreversibile", "Procedendo eliminerai il tuo materiale in prestito per sempre e non potrai più recuperarlo. Procedere?");
                    });
                    /*renter.setText(material.getRenter().getName());
                    renterLayout.setVisibility(VISIBLE);
                    if (material.getLandig().getEndExpiryDate() != null) {
                        requestDate.setText(Utils.formatDateToString(material.getLanding().getEndExpiryDate()));
                        extensionLayout.setVisibility(VISIBLE);
                        acceptExtension.setOnClickListener(v -> {
                            LendingInProgress p = material.getLending();
                            material.updateExpiryDate(p.getEndExpiryDate().toDate());
                            p.updateEndExpiryDate(null);
                            date.setText(Utils.formatDateToString(p.getEndExpiryDate().toDate()));
                            extensionLayout.setVisibility(View.GONE);
                        });
                        rejectExtension.setOnClickListener(v -> {
                            LendingInProgress p = material.getLending();
                            p.updateEndExpiryDate(null);
                        });
                    }*/
                }
                break;
            case MyRentedMaterialsFragment.ID:

                break;
            case ShowcaseFragment.ID:
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
                                        AtomicReference<LendingInProgress> l = null;
                                        Thread t = new Thread(() -> {
                                            try {
                                                material.updateRenter(user);
                                                l.set(LendingInProgress.createLendingInProgress(material, material.getExpiryDate().toDate()));
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
                                        res.putExtra("LendingID", l.get().getId());
                                        setResult(done.get() ? RENT_SUCCESS : RENT_FAIL, res);
                                        finish();
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                })
                                .show(getString(R.string.rent_disclosure), getString(R.string.rent_disclosure_description));
                });
                break;
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
}