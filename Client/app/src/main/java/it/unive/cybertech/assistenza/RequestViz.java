package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.obtainQuarantineAssistanceById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.Chat;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.messages.MessageService;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Showables;
import it.unive.cybertech.utils.Utils;

/**
 * The request visualization shows an extended version of the one found previously in {@link it.unive.cybertech.assistenza.HomePageNegative}
 * and {@link it.unive.cybertech.assistenza.HomePagePositive} listView, allowing users different interaction
 * depending on the roles (positive or negative to COVID-19).
 *
 * .Sick users can delete the request they made or access to the chat with their volunteer
 * .Volunteer users can accept the request if they want to help with the given task, stop helping an already
 * taken request, or access the chat if they need additional information
 *
 * @author Mihail Racaru
 * @since 1.1
 */
public class RequestViz extends AppCompatActivity {
    private final User user = CachedUser.user;
    private FloatingActionButton menu, chat, deleteRequest, accept_request, stop_helping;
    private TextView textTitle, text, textCountry, textCity, textDate;
    private Animation menuOpen, menuClose;
    boolean isOpen = false;
    private String id, idInCharge, callerClass;
    private QuarantineAssistance request;
    private User target;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visualisation);

        toolbar();
        findingLayoutElements();
        animations();
        getStringExtra();

        if (id != null || idInCharge != null || callerClass != null) {
            initializeRequest(id, idInCharge);
            setFields();

            if(idInCharge != null) {
                String taken = "taken";

                menu.setOnClickListener(v -> {
                    animatedMenu(taken);
                });

                chat.setOnClickListener(v -> {
                    openChat();
                });

                stop_helping.setOnClickListener(v -> {
                    stop_helping();
                });
            }

            if (callerClass != null && callerClass.equals("Homenegative")) {
                String idTaken = getIntent().getStringExtra("alreadyTaken");
                if(idTaken == null) {
                    menu.setOnClickListener(v -> {
                        animatedMenu(callerClass);
                    });

                    accept_request.setOnClickListener(v -> {
                        accept_request();
                    });
                }
                else
                    menu.setVisibility(View.GONE);
            }

            if(callerClass != null && callerClass.equals("positive")) {

                menu.setOnClickListener(v -> {
                    animatedMenu(callerClass);
                });

                deleteRequest.setOnClickListener(v -> {
                    delete_request();
                });

                if (request.getIsInCharge()) {
                    chat.setOnClickListener(v -> {
                        openChat();
                    });
                }
            }

        }
        else {
            new Utils.Dialog(this)
                    .hideCancelButton()
                    .setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }

                        @Override
                        public void onCancel() {

                        }
                    })
                    .show(getString(R.string.no_inCharge), getString(R.string.no_inCharge_message));
            String allGone = "allGone";
            animatedMenu(allGone);
        }
    }

    /**
     * This function open the chat activity retrieving the chat ID and passing it to the activity
     * */
    private void openChat(){
        chat.setEnabled(false);
        Utils.executeAsync(() -> request.obtainMaterializeChat(), new Utils.TaskResult<Chat>() {
            @Override
            public void onComplete(Chat result) {
                Intent i =new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("ID", result.getId());
                startActivity(i);
                chat.setEnabled(true);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                chat.setEnabled(true);
                Showables.showShortToast(getString(R.string.error_opening_chat), getApplicationContext());
            }
        });

    }

    /**
     * Users can delete their {@link #request}, a notification is sent to the user who took in charge
     * the task eventually
     * @author Mihail Racaru
     * @since 1.1
     */
    private void delete_request() {
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.show(getString(R.string.attention), getString(R.string.delete_request_warning));
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
                Thread t = new Thread(() -> {
                    try {
                        target = request.obtainMaterializeInCharge();
                    } catch (ExecutionException | NoQuarantineAssistanceFoundException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(target != null)
                        sendNotifications(target, "delete", user.getName());
                    user.removeQuarantineAssistance(request);
                    setResult(Activity.RESULT_OK);
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException ignored) {
                }
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    /**
     * Volunteers can accept a helping request, only if there is not one already being followed,
     * a notification is sent to the owner of the {@link #request}, warning him an user took in charge their task
     * @author Mihail Racaru
     * @since 1.1
     */
    private void accept_request() {
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.show(getString(R.string.information), getString(R.string.request_taken_in_charge));
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
                Utils.executeAsync(() -> request.updateInCharge_QuarantineAssistance(user), new Utils.TaskResult<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) {
                        Thread t = new Thread(() -> {
                            try {
                                target = request.obtainRequestOwner();
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

                        sendNotifications(target, "accept", user.getName());
                        setResult(Activity.RESULT_OK);
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    /**
     * Volunteers can stop following a request they accepted, so other people can take it in charge,
     * a notification is send to the owner of the {@link #request}, warning him of the event
     *
     * @author Mihail Racaru
     * @since 1.1
     */
    private void stop_helping() {
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.show(getString(R.string.attention), getString(R.string.stop_helping_request));
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
                Utils.executeAsync(() -> request.updateInCharge_QuarantineAssistance(null), new Utils.TaskResult<Boolean>() {
                    @Override
                    public void onComplete(Boolean result){
                        Thread t = new Thread(() -> {
                            try {
                                target = request.obtainRequestOwner();
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
                        sendNotifications(target, "stop", user.getName());
                        setResult(Activity.RESULT_OK);
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    /**
     * Check if the user is following already a help request, side effects the reference {@link #request} accordingly
     * @param id for the request selected
     * @param idInCharge for the request that user is already following, if exists
     * @author Mihail Racaru
     * @since 1.1
     */
    private void initializeRequest(String id, String idInCharge) {
        if(id != null || idInCharge != null) {
            Thread t = new Thread(() -> {
                try {
                    request = obtainQuarantineAssistanceById(idInCharge == null ? id : idInCharge);
                } catch (ExecutionException | NoQuarantineAssistanceFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize the TextView
     * @author Mihail Racaru
     * @since 1.1
     */
    private void setFields() {
        String title = getIntent().getStringExtra("title");
        textTitle.setText(title);

        String country = getIntent().getStringExtra("country");
        textCountry.setText(country);

        String city = getIntent().getStringExtra("city");
        textCity.setText(city);

        String strDate = Utils.formatDateToString(request.obtainDeliveryDateToDate(), "kk:mm  dd/MM");
        textDate.setText(strDate);

        text.setText(request.getDescription());
    }

    /**
     * Sets {@link #id}, {@link #idInCharge}, {@link #callerClass} to the id of the request selected,
     * the request that user has already accepted and the caller class.
     * @author Mihail Racaru
     * @since 1.1
     */
    private void getStringExtra() {
        id = getIntent().getStringExtra("id");
        idInCharge = getIntent().getStringExtra("user");
        callerClass = getIntent().getStringExtra("class");
    }

    /**
     * Initialize the menu FloatingActionButton animation for its opening and closure
     * @author Mihail Racaru
     * @since 1.1
     */
    private void animations() {
        menuOpen = AnimationUtils.loadAnimation(this, R.anim.from_botton_animation);
        menuClose = AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation);
    }

    /**
     * Finds and sets the toolbar
     * @author Mihail Racaru
     * @since 1.1
     */
    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_RequestViz);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("Dettagli richiesta");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Finds all layout elements
     * @author Mihail Racaru
     * @since 1.1
     */
    private void findingLayoutElements() {
        textTitle = findViewById(R.id.textTitle);
        text = findViewById(R.id.textFull);
        textCountry = findViewById(R.id.textCountry);
        textCity = findViewById(R.id.textCity);
        textDate = findViewById(R.id.textDate);

        menu = findViewById(R.id.menu_open);
        chat = findViewById(R.id.chat_from_request);
        accept_request = findViewById(R.id.acceptRequest);
        stop_helping = findViewById(R.id.stopHelping);
        deleteRequest = findViewById(R.id.deleteRequest);
    }

    /**
     * Depending on the caller, sets the open and close animation and the visibility to the right FloatingActionButton
     * @param caller identify the previous activity who called RequestViz
     * @author Mihail Racaru
     * @since 1.1
     */
    private void animatedMenu(@NonNull String caller) {
        if(caller.equals("positive")) {
            if (isOpen) {
                if(request.getIsInCharge()) {
                    chat.startAnimation(menuClose);
                    deleteRequest.startAnimation(menuClose);
                    chat.setClickable(false);
                    deleteRequest.setClickable(false);
                    chat.setVisibility(View.GONE);
                    deleteRequest.setVisibility(View.GONE);
                }
                else {
                    deleteRequest.startAnimation(menuClose);
                    deleteRequest.setClickable(false);
                    deleteRequest.setVisibility(View.GONE);
                }

                isOpen = false;
            } else {
                if(request.getIsInCharge()) {
                    chat.setVisibility(View.VISIBLE);
                    deleteRequest.setVisibility(View.VISIBLE);
                    chat.startAnimation(menuOpen);
                    deleteRequest.startAnimation(menuOpen);
                    chat.setClickable(true);
                    deleteRequest.setClickable(true);
                }
                else {
                    deleteRequest.setVisibility(View.VISIBLE);
                    deleteRequest.startAnimation(menuOpen);
                    deleteRequest.setClickable(true);
                }
                isOpen = true;
            }
        }

        if(caller.equals("Homenegative")) {
            if(isOpen) {
                accept_request.startAnimation(menuClose);
                accept_request.setClickable(false);
                accept_request.setVisibility(View.GONE);
            }
            else {
                accept_request.startAnimation(menuOpen);
                accept_request.setClickable(true);
                accept_request.setVisibility(View.VISIBLE);
            }
        }

        if(caller.equals("taken")) {
            if(isOpen) {
                chat.startAnimation(menuClose);
                chat.setClickable(false);
                chat.setVisibility(View.GONE);

                stop_helping.startAnimation(menuClose);
                stop_helping.setClickable(false);
                stop_helping.setVisibility(View.GONE);
            }
            else {
                chat.startAnimation(menuOpen);
                chat.setClickable(true);
                chat.setVisibility(View.VISIBLE);

                stop_helping.startAnimation(menuOpen);
                stop_helping.setClickable(true);
                stop_helping.setVisibility(View.VISIBLE);
            }
        }
        if(caller.equals("allGone")) {
            findViewById(R.id.textTitle).setVisibility(View.GONE);
            findViewById(R.id.textFull).setVisibility(View.GONE);
            findViewById(R.id.textCountry).setVisibility(View.GONE);
            findViewById(R.id.textCity).setVisibility(View.GONE);
            findViewById(R.id.textDate).setVisibility(View.GONE);
            findViewById(R.id.menu_open).setVisibility(View.GONE);
        }
    }

    /**
     * Create the right notification based on the event caller
     * @param user identify the target of the notification
     * @param event specify the notification to be send
     * @param name indicates the Name of the persone that took in charge, or stopped helping or deleted the request
     * @author Mihail Racaru
     * @since 1.1
     */
    private void sendNotifications(User user, String event, String name) {
        switch (event) {
            case "accept":
                MessageService.sendMessageToUserDevices(user, MessageService.NotificationType.request_accepted,
                        getString(R.string.request_taken), name +" "+getString(R.string.request_taken_notification),
                        this);
            case "stop":
                MessageService.sendMessageToUserDevices(user, MessageService.NotificationType.request_stop_helping,
                        getString(R.string.attention), name + " "+getString(R.string.stop_helping_notification),
                        this);
            case "delete":
                MessageService.sendMessageToUserDevices(user, MessageService.NotificationType.request_stop_helping,
                        getString(R.string.attention), name + " "+getString(R.string.request_deleted_notification),
                        this);
        }
    }
}
