package it.unive.cybertech.noleggio;

import static android.view.View.VISIBLE;
import static it.unive.cybertech.utils.CachedUser.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.Collections2;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;
import it.unive.cybertech.database.Profile.Exception.NoRentMaterialFoundException;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

/**
 * This class has 5 functions because it's the class that shows the material to the user but it also manage the different operation based on the material situation
 *
 * <p>
 * 1) The product is in the showcase and the user is not the owner of the material
 * In this case will be showed a button to rent this material
 * </p>
 * <p>
 * 2) The product is in the showcase or other pages but it's owned by the current user.
 * In this case the user can manage the material, remove it (if nobody rented) or accept an eventual extension request
 * </p>
 * <p>
 * 3) The material is in rented section
 * In this case, the user can require an extension of the lending
 * </p>
 * <p>
 * 4) The lending (form a renter) is expired and need to be delivered to the owner
 * In this case the user have to confirm it's delivery
 * </p>
 * <p>
 * 5) The lending (form the owner) is expired and the delivery is done
 * In this case, the owner can confirm the delivery and provide the feedback about the material's treatment
 * </p>
 * In every case, the material and the relative lending (if any) will be loaded
 */
public class ProductDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Material material;
    private LendingInProgress lending;
    private String id, from;
    private ImageView photo;
    private TextView title, description, expireDateMaterial, renter, requestDate, extensionDateRequest, dateDescription, expireDateRent;
    private FloatingActionButton delete, confirm, extend, complete;
    private ConstraintLayout renterLayout, extensionLayout;
    private Button acceptExtension, rejectExtension;
    static final int EXCEPTION = -2;
    static final int RENT_DELETE = -1;
    static final int SUCCESS = 1;
    static final int RENT_FAIL = 0;
    static final int RENT_TERMINATED = 2;
    static final int FEEDBACK = 0;
    private int pos;
    private ActionBar actionBar;
    private Context context;
    private LinearLayout extensionRenterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_noleggio);
        Intent i = getIntent();
        id = i.getStringExtra("ID");
        pos = i.getIntExtra("Position", -1);
        from = i.getStringExtra("Type");
        context = this;
        if (id == null || id.isEmpty())
            finish();
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        extensionRenterLayout = findViewById(R.id.extension_renter_details_layout);
        confirm = findViewById(R.id.confirm_rent_showcase);
        acceptExtension = findViewById(R.id.accept_extension_details);
        rejectExtension = findViewById(R.id.reject_extension_details);
        extensionDateRequest = findViewById(R.id.extension_date_details_showcase);
        complete = findViewById(R.id.complete_lending);
        extend = findViewById(R.id.extend_lending_fab);
        delete = findViewById(R.id.delete_rent_showcase);
        photo = findViewById(R.id.product_image_details_showcase);
        title = findViewById(R.id.title_details_showcase);
        description = findViewById(R.id.description_details_showcase);
        expireDateMaterial = findViewById(R.id.expiry_materials_date_product_details);
        expireDateRent = findViewById(R.id.expiry_rent_date_product_details);
        dateDescription = findViewById(R.id.date_description_product_details);
        renterLayout = findViewById(R.id.renter_layout_details);
        extensionLayout = findViewById(R.id.extension_layout_details);
        renter = findViewById(R.id.user_name_renter_details);
        requestDate = findViewById(R.id.expire_date_rent_details);
        renterLayout.setVisibility(View.GONE);
        extensionLayout.setVisibility(View.GONE);
        if (from.equals(MyRentMaterialsFragment.ID))
            confirm.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromDB(new Utils.TaskResult<Void>() {
            @Override
            public void onComplete(Void result) {
                try {
                    manageType();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    finishWithError();
                }
            }

            @Override
            public void onError(Exception e) {
                finishWithError();
            }
        });
    }

    /**
     * The main function that manage the visualization of the product
     *
     * @throws InterruptedException something went wrong retrieving data
     */
    private void manageType() throws InterruptedException {
        expireDateMaterial.setText(Utils.formatDateToString(material.getExpiryDate().toDate()));
        Utils.executeAsync(() -> material.getMaterializedRenter(), new Utils.TaskResult<User>() {
            @Override
            public void onComplete(User renterUser) {
                //If the renter is not null, it means that someone has rented the material
                if (renterUser != null) {
                    //Set renter name
                    renter.setText(renterUser.getName() + " " + renterUser.getSurname());
                    //If a lending is in progress (as it should due to the fact that the runter exists)
                    if (lending != null) {
                        //If the lending is not expired
                        if (lending.getExpiryDate().compareTo(Timestamp.now()) > 0) {
                            //If the current user is the owner, then manage the owner layout
                            if (material.getOwner().getId().equals(user.getId())) {
                                renterLayout.setVisibility(VISIBLE);
                                expireDateRent.setText(Utils.formatDateToString(lending.getExpiryDate().toDate()));
                                //If an extension request has been made, show the relative layout
                                if (lending.getEndExpiryDate() != null) {
                                    requestDate.setText(Utils.formatDateToString(lending.getEndExpiryDate().toDate()));
                                    extensionLayout.setVisibility(VISIBLE);
                                    //When the renter has made an extension request an the user wants to accept it
                                    acceptExtension.setOnClickListener(v -> {
                                        //Update the lending end date
                                        Utils.executeAsync(() -> lending.updateExpiryDate(lending.getEndExpiryDate().toDate()), new Utils.TaskResult<Boolean>() {
                                            @Override
                                            public void onComplete(Boolean result) {
                                                if (result)
                                                    Utils.executeAsync(() -> lending.updateEndExpiryDate(null), new Utils.TaskResult<Boolean>() {
                                                        @Override
                                                        public void onComplete(Boolean result) {
                                                            if (result) {
                                                                expireDateRent.setText(Utils.formatDateToString(lending.getExpiryDate().toDate()));
                                                                extensionLayout.setVisibility(View.GONE);
                                                                Snackbar.make(findViewById(android.R.id.content), context.getString(R.string.request_accepted), Snackbar.LENGTH_LONG).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    });
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    });
                                    //When the renter has made an extension request an the user wants to reject it
                                    rejectExtension.setOnClickListener(v -> {
                                        Utils.executeAsync(() -> lending.updateEndExpiryDate(null), new Utils.TaskResult<Boolean>() {
                                            @Override
                                            public void onComplete(Boolean result) {
                                                extensionLayout.setVisibility(View.GONE);
                                                Snackbar.make(findViewById(android.R.id.content), context.getString(R.string.request_rejected), Snackbar.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    });
                                }
                                //If the user is not the owner (so it's the renter)
                            } else {
                                expireDateMaterial.setText(Utils.formatDateToString(lending.getDateExpiryDate()));
                                extend.setVisibility(VISIBLE);
                                //Show the rent extension request layout
                                extend.setOnClickListener(v -> {
                                    new Utils.Dialog(context)
                                            .setCallback(new Utils.DialogResult() {
                                                @Override
                                                public void onSuccess() {
                                                    //Start the datepicjer
                                                    Calendar now = Calendar.getInstance();
                                                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                                                            context, ProductDetails.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                                                    datePickerDialog.getDatePicker().setMaxDate(material.getExpiryDate().toDate().getTime());
                                                    datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
                                                    datePickerDialog.show();
                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            })
                                            .show(getString(R.string.rent_extension), getString(R.string.rent_extension_request_description));
                                });
                            }
                            //If the lending is not expired
                        } else {
                            expireDateRent.setTextColor(getColor(R.color.red_fs));
                            //Check if the user is the owner
                            if (material.getOwner().getId().equals(user.getId())) {
                                renterLayout.setVisibility(VISIBLE);
                                expireDateRent.setText(Utils.formatDateToString(lending.getExpiryDate().toDate()));
                                //If the lending has been delivered, shows the button to complete the lending
                                if (lending.getWaitingForFeedback()) {
                                    complete.setVisibility(VISIBLE);
                                    complete.setOnClickListener(v -> {
                                        //if (lending.getWaitingForFeedback()) {
                                        new Utils.Dialog(context)
                                                .setCallback(new Utils.DialogResult() {
                                                    @Override
                                                    public void onSuccess() {
                                                        //Start the activity for the feedback
                                                        startActivityForResult(new Intent(context, RentFeedback.class), FEEDBACK);
                                                    }

                                                    @Override
                                                    public void onCancel() {

                                                    }
                                                }).show(getString(R.string.rent_terminated), getString(R.string.rent_terminated_feedback_required));
                                        /*} else
                                            new Utils.Dialog(context)
                                                    .hideCancelButton()
                                                    .show("Attesa di restituzione", "L'utente non ha ancora confermato di aver restituito il materiale. Fino ad allora non potrai terminare il rpestito.");*/
                                    });
                                } else {
                                    expireDateRent.append(" (" + getString(R.string.waiting_for_feedback) + ")");
                                }
                                //If i'm not the renter, show the button to confirm the product delivery
                            } else {
                                complete.setVisibility(VISIBLE);
                                complete.setOnClickListener(v -> {
                                    new Utils.Dialog(context)
                                            .setCallback(new Utils.DialogResult() {
                                                @Override
                                                public void onSuccess() {
                                                    Thread t = new Thread(() -> {
                                                        lending.updateWaitingForFeedback(true);
                                                    });
                                                    t.start();
                                                    try {
                                                        t.join();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Intent res = new Intent();
                                                    res.putExtra("Position", pos);
                                                    setResult(RENT_TERMINATED, res);
                                                    finish();
                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            }).show(getString(R.string.rent_terminated), getString(R.string.rent_terminated_feedback_required_renter));
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                if (e instanceof NoRentMaterialFoundException) {
                    expireDateMaterial.setText(Utils.formatDateToString(material.getExpiryDate().toDate()));
                    //If the error is that the lending doesn't exists and i'm the owner, the user can delete it's material
                    if (material.getOwner().getId().equals(user.getId())) {
                        delete.setVisibility(VISIBLE);
                        delete.setOnClickListener(v -> {
                            new Utils.Dialog(context)
                                    .setCallback(new Utils.DialogResult() {
                                        @Override
                                        public void onSuccess() {
                                            Utils.executeAsync(() -> user.removeMaterial(material), new Utils.TaskResult<Boolean>() {
                                                @Override
                                                public void onComplete(Boolean result) {
                                                    Thread t = new Thread(material::deleteMaterial);
                                                    t.start();
                                                    try {
                                                        t.join();
                                                    } catch (InterruptedException interruptedException) {
                                                        interruptedException.printStackTrace();
                                                    }
                                                    Intent res = new Intent();
                                                    res.putExtra("Position", pos);
                                                    setResult(RENT_DELETE, res);
                                                    finish();
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    })
                                    .show(getString(R.string.irreversible_operation), getString(R.string.delete_material_confirm));
                        });
                    } else {
                        //If i'm the renter, shows the rent button
                        confirm.setVisibility(VISIBLE);
                        confirm.setOnClickListener(view -> {
                            //If the user reliability is lower than 0
                            if (user.getLendingPoint() < 0)
                                new Utils.Dialog(context)
                                        .hideCancelButton()
                                        .show(getString(R.string.unreliable), getString(R.string.unreliable_description));
                            else
                                //else show the disclosure
                                new Utils.Dialog(context)
                                        .setCallback(new Utils.DialogResult() {
                                            @Override
                                            public void onSuccess() {
                                                Utils.executeAsync(() -> {
                                                    material.updateRenter(user);
                                                    LendingInProgress l = LendingInProgress.createLendingInProgress(material, material.getExpiryDate().toDate());
                                                    user.addLending(l);
                                                    return l;
                                                }, new Utils.TaskResult<LendingInProgress>() {
                                                    @Override
                                                    public void onComplete(LendingInProgress result) {
                                                        Intent res = new Intent();
                                                        res.putExtra("Position", pos);
                                                        res.putExtra("LendingID", result.getId());
                                                        setResult(result != null ? SUCCESS : RENT_FAIL, res);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        e.printStackTrace();
                                                        Intent res = new Intent();
                                                        setResult(RENT_FAIL, res);
                                                        finish();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancel() {

                                            }
                                        })
                                        .show(getString(R.string.rent_disclosure), getString(R.string.rent_disclosure_description));
                        });
                    }
                }
                e.printStackTrace();
            }
        });
    }

    /**
     * Retrive the material from the database and call the callback when it's done
     *
     * @param id       The material id
     * @param callback The callback to call when the data has been retrieved
     */
    private void getMaterial(@NonNull String id, @NonNull Utils.TaskResult<Void> callback) {
        Utils.executeAsync(() -> Material.getMaterialById(id), new Utils.TaskResult<Material>() {
            @Override
            public void onComplete(Material result) {
                material = result;
                actionBar.setTitle("Dettaglio: " + material.getTitle());
                title.setText(material.getTitle());
                description.setText(material.getDescription());
                //date.setText(Utils.formatDateToString(material.getExpiryDate().toDate()));
                if (material.getPhoto() != null) {
                    byte[] arr = Base64.decode(material.getPhoto(), Base64.DEFAULT);
                    if (arr != null && arr.length > 0)
                        photo.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
                }
                callback.onComplete(null);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                finishWithError();
            }
        });
    }

    /**
     * Retrieve the lending from the database and call the callback when it's done
     *
     * @param id       The material id
     * @param callback The callback to call when the data has been retrieved
     */
    private void getLending(@NonNull String id, @NonNull Utils.TaskResult<Void> callback) {
        Utils.executeAsync(() -> LendingInProgress.getLendingInProgressById(id), new Utils.TaskResult<LendingInProgress>() {
            @Override
            public void onComplete(LendingInProgress result) {
                lending = result;
                getMaterial(lending.getMaterial().getId(), callback);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                finishWithError();
            }
        });
    }

    /**
     * Retrieve data from the database
     * It checks where the ID come from in order to know if the ID passed is a material ID or a lending ID
     *
     * @param callback A callback to call when the data retrieving is done
     */
    private void getDataFromDB(@NonNull Utils.TaskResult<Void> callback) {
        ///It's a material, so call the get material function
        if (from.equals(ShowcaseFragment.ID) || from.equals(MyRentMaterialsFragment.ID))
            getMaterial(id, new Utils.TaskResult<Void>() {
                @Override
                public void onComplete(Void result) {
                    ///After the material we should get the lending (if any)
                    Utils.executeAsync(() -> material.getLending(), new Utils.TaskResult<LendingInProgress>() {
                        @Override
                        public void onComplete(LendingInProgress result) {
                            lending = result;
                            callback.onComplete(null);
                        }

                        @Override
                        public void onError(Exception e) {
                            if (e instanceof NoLendingInProgressFoundException)
                                callback.onComplete(null);
                            else
                                callback.onError(e);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });
        else
            ///It's a lending, so call the get lending function
            getLending(id, new Utils.TaskResult<Void>() {
                @Override
                public void onComplete(Void result) {
                    ///After a lending we have to retrieve the material (always exists)
                    getMaterial(lending.getMaterial().getId(), callback);
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar pickedDate = Calendar.getInstance();
        pickedDate.set(year, month, dayOfMonth);
        ///Shows a dialog to confirm the rent extension request
        new Utils.Dialog(this)
                .setCallback(new Utils.DialogResult() {
                    @Override
                    public void onSuccess() {
                        ///If confirmed, then update the database
                        Utils.executeAsync(() -> lending.updateEndExpiryDate(pickedDate.getTime()), new Utils.TaskResult<Boolean>() {
                            @Override
                            public void onComplete(Boolean result) {
                                if (result) {
                                    extensionRenterLayout.setVisibility(VISIBLE);
                                    extensionDateRequest.setText(Utils.formatDateToString(lending.getEndExpiryDate().toDate()));
                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.request_sended), Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_sending_request), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show(getString(R.string.rent_extension), getString(R.string.confirm_rent_extension) + Utils.formatDateToString(pickedDate.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ///If the feedback activity finished
        if (requestCode == FEEDBACK) {
            ///Check if returns a successful code
            if (resultCode == RentFeedback.SUCCESS) {
                ///Get the calculated points
                double score = data.getDoubleExtra("Points", 0);
                //Update the user total lending point and remove the lending from the database
                Utils.executeAsync(() -> User.getUserById(material.getRenter().getId()), new Utils.TaskResult<User>() {
                    @Override
                    public void onComplete(User result) {
                        Thread t = new Thread(() -> {
                            result.updateLendingPoint((long) (result.getLendingPoint() + score));
                            try {
                                List<LendingInProgress> temp = result.getExpiredLending();
                                lending = Collections2.filter(temp, o -> o.getMaterial().getId().equals(material.getId())).iterator().next();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            result.removeLending(lending);
                            lending.removeLendingInProgress();
                            material.updateRenter(null);
                        });
                        t.start();
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent res = new Intent();
                        res.putExtra("Position", pos);
                        //Notify the caller that the lending ended successfully
                        setResult(RENT_TERMINATED, res);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            } else {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.something_wrong), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Finish the activity and set the error
     */
    private void finishWithError() {
        setResult(EXCEPTION, null);
        finish();
    }
}