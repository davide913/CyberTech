package it.unive.cybertech.noleggio;

import static android.view.View.VISIBLE;
import static it.unive.cybertech.utils.CachedUser.user;

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

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.Exception.NoLendingInProgressFoundException;

import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

public class ProductDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Material material;
    private LendingInProgress lending;
    private String id, from;
    private ImageView photo;
    private TextView title, description, date, renter, requestDate, extensionDateRequest, dateDescription;
    private FloatingActionButton delete, confirm, extend, complete;
    private ConstraintLayout renterLayout, extensionLayout;
    private Button acceptExtension, rejectExtension;
    static final int EXCEPTION = -2;
    static final int RENT_DELETE = -1;
    static final int RENT_SUCCESS = 1;
    static final int RENT_FAIL = 0;
    static final int RENT_TERMINATED = 2;
    static final int FEEDBACK = 0;
    private int pos;
    private ActionBar actionBar;
    private Context context;
    private LinearLayout extensionRenterLayout;

    //cambiare tutta la logica. Prendere sempre il lending e il material e gestire se è null o meno con progress bar
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
        date = findViewById(R.id.expiring_date_details_showcase);
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
                /*if (lending != null)
                    date.setText(Utils.formatDateToString(lending.getDateExpiryDate()));
                else
                    date.setText(Utils.formatDateToString(material.getExpiryDate().toDate()));*/
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

    private void manageType() throws InterruptedException {
        switch (from) {
            case MyRentMaterialsFragment.ID:
                Utils.executeAsync(() -> material.obtainMaterializedRenter(), new Utils.TaskResult<User>() {
                    @Override
                    public void onComplete(User renterUser) {
                        if (renterUser != null) {
                            renter.setText(renterUser.getName() + " " + renterUser.getSurname());
                            renterLayout.setVisibility(VISIBLE);
                            if (lending != null) {
                                date.setText(Utils.formatDateToString(lending.getDateExpiryDate()));
                                if (lending.getEndExpiryDate() != null) {
                                    requestDate.setText(Utils.formatDateToString(lending.getEndExpiryDate().toDate()));
                                    extensionLayout.setVisibility(VISIBLE);
                                    acceptExtension.setOnClickListener(v -> {
                                        Utils.executeAsync(() -> lending.updateExpiryDate(lending.getEndExpiryDate().toDate()), new Utils.TaskResult<Boolean>() {
                                            @Override
                                            public void onComplete(Boolean result) {
                                                if (result)
                                                    Utils.executeAsync(() -> lending.updateEndExpiryDate(null), new Utils.TaskResult<Boolean>() {
                                                        @Override
                                                        public void onComplete(Boolean result) {
                                                            if (result) {
                                                                date.setText(Utils.formatDateToString(lending.getExpiryDate().toDate()));
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
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (e instanceof NoRentMaterialFoundException) { //TODO ENRICO
                            dateDescription.setText(R.string.showcase_until_dotted);
                            date.setText(Utils.formatDateToString(material.getExpiryDate().toDate()));
                            delete.setVisibility(VISIBLE);
                            delete.setOnClickListener(v -> {
                                new Utils.Dialog(context)
                                        .setCallback(new Utils.DialogResult() {
                                            @Override
                                            public void onSuccess() {
                                                Utils.executeAsync(() -> material.deleteMaterial(), new Utils.TaskResult<Boolean>() {
                                                    @Override
                                                    public void onComplete(Boolean result) {
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
                                        .show("Operazione irreversibile", "Procedendo eliminerai il tuo materiale in prestito per sempre e non potrai più recuperarlo. Procedere?");
                            });
                        }
                        e.printStackTrace();
                    }
                });
                break;
            case MyRentedMaterialsFragment.ID:
                confirm.setVisibility(View.GONE);
                if (lending.getEndExpiryDate() != null) {
                    extensionRenterLayout.setVisibility(VISIBLE);
                    extensionDateRequest.setText(Utils.formatDateToString(lending.getEndExpiryDate().toDate()));
                }
                extend.setVisibility(VISIBLE);
                extend.setOnClickListener(v -> {
                    new Utils.Dialog(context)
                            .setCallback(new Utils.DialogResult() {
                                @Override
                                public void onSuccess() {
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
                            .show("Estensione prestito", "Continuando ti verrà chiesta la data ultima di consegna che desideri. Sarà cura dell'utente accettare o rifiutare la proposta");
                });
                break;
            case ShowcaseFragment.ID:
                confirm.setVisibility(VISIBLE);
                date.setText(Utils.formatDateToString(material.getExpiryDate().toDate()));
                confirm.setOnClickListener(view -> {
                    if (user.getLendingPoint() < 0)
                        new Utils.Dialog(context)
                                .hideCancelButton()
                                .show(getString(R.string.unreliable), getString(R.string.unreliable_description));
                    else
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
                                                setResult(result != null ? RENT_SUCCESS : RENT_FAIL, res);
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
                break;
            case RentMaterialAdapter.ID:
                Thread t = new Thread(() -> {
                    try {
                        List<LendingInProgress> temp = material.obtainMaterializedRenter().obtainMaterializedLendingInProgress();
                        lending = Collections2.filter(temp, o -> o.getMaterial().getId().equals(material.getId())).iterator().next();
                        if (lending == null)
                            throw new RuntimeException("Lending is null");
                    } catch (Exception e) {
                        e.printStackTrace();
                        finishWithError();
                    }
                });
                t.start();
                try {
                    t.join();
                    complete.setVisibility(VISIBLE);
                    complete.setOnClickListener(v -> {
                        if (lending.getWaitingForFeedback()) {
                            new Utils.Dialog(context)
                                    .setCallback(new Utils.DialogResult() {
                                        @Override
                                        public void onSuccess() {
                                            startActivityForResult(new Intent(context, RentFeedback.class), FEEDBACK);
                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    }).show("Prestito terminato", "Prima di concludere ci serve il tuo feedback");
                        } else
                            new Utils.Dialog(context)
                                    .hideCancelButton()
                                    .show("Attesa di restituzione", "L'utente non ha ancora confermato di aver restituito il materiale. Fino ad allora non potrai terminare il rpestito.");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case RentedMaterialsAdapter.ID:
                complete.setVisibility(VISIBLE);
                complete.setOnClickListener(v -> {
                    new Utils.Dialog(context)
                            .setCallback(new Utils.DialogResult() {
                                @Override
                                public void onSuccess() {
                                    //TODO update flag rent
                                    Utils.executeAsync(() -> lending.updateWaitingForFeedback(true), new Utils.TaskResult<Boolean>() {
                                        @Override
                                        public void onComplete(Boolean result) {
                                            if (result) {
                                                Intent res = new Intent();
                                                res.putExtra("Position", pos);
                                                setResult(RENT_TERMINATED, res);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancel() {

                                }
                            }).show("Prestito terminato", "Vuoi concludere il prestito?");
                });
                break;
        }
    }

    private void getMaterial(String id, Utils.TaskResult<Void> callback) {
        Utils.executeAsync(() -> Material.obtainMaterialById(id), new Utils.TaskResult<Material>() {
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

    private void getLending(String id, Utils.TaskResult<Void> callback) {
        Utils.executeAsync(() -> LendingInProgress.obtainLendingInProgressById(id), new Utils.TaskResult<LendingInProgress>() {
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

    private void getDataFromDB(Utils.TaskResult<Void> callback) {
        if (from.equals(ShowcaseFragment.ID) || from.equals(MyRentMaterialsFragment.ID) || from.equals(RentMaterialAdapter.ID))
            getMaterial(id, new Utils.TaskResult<Void>() {
                @Override
                public void onComplete(Void result) {
                    Utils.executeAsync(() -> material.obtainLending(), new Utils.TaskResult<LendingInProgress>() {
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
                    //getLending(material.getLending(),callback);
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });
        else
            getLending(id, new Utils.TaskResult<Void>() {
                @Override
                public void onComplete(Void result) {
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
        new Utils.Dialog(this)
                .setCallback(new Utils.DialogResult() {
                    @Override
                    public void onSuccess() {
                        Utils.executeAsync(() -> lending.updateEndExpiryDate(pickedDate.getTime()), new Utils.TaskResult<Boolean>() {
                            @Override
                            public void onComplete(Boolean result) {
                                if (result) {
                                    extensionRenterLayout.setVisibility(VISIBLE);
                                    extensionDateRequest.setText(Utils.formatDateToString(lending.getEndExpiryDate().toDate()));
                                    Snackbar.make(findViewById(android.R.id.content), "Richiesta inviata con successo", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Snackbar.make(findViewById(android.R.id.content), "Errore nell'invio della richiesta", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show("Estensione prestito", "Confermi la nuova data di restituzione: " + Utils.formatDateToString(pickedDate.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FEEDBACK) {
            if (resultCode == RentFeedback.SUCCESS) {
                double score = data.getDoubleExtra("Points", 0);
                Utils.executeAsync(() -> User.obtainUserById(material.getRenter().getId()), new Utils.TaskResult<User>() {
                    @Override
                    public void onComplete(User result) {
                        Thread t = new Thread(() -> {
                            result.updateLendingPoint((long) (result.getLendingPoint() + score));
                            try {
                                List<LendingInProgress> temp = result.obtainMaterializedLendingInProgress();
                                lending = Collections2.filter(temp, o -> o.getMaterial().getId().equals(material.getId())).iterator().next();
                                result.removeLending(lending);
                                lending.deleteLendingInProgress();
                            } catch (Exception e) {
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
                        setResult(RENT_TERMINATED, res);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            } else {

            }
        }
    }

    private void finishWithError() {
        setResult(EXCEPTION, null);
        finish();
    }
}