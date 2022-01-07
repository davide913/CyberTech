package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.obtainQuarantineAssistanceById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.messages.MessageService;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;

public class RequestViz extends AppCompatActivity {
    private final User user = CachedUser.user;
    private FloatingActionButton menu, chat, deleteRequest, accept_request, stop_helping;
    private TextView textTitle, text, textCountry, textCity, textDate;
    Toolbar toolbar;
    private Animation menuOpen, menuClose;
    boolean isOpen = false;
    private String id, idInCharge, callerClass;
    private String title, country, city, strDate;
    private QuarantineAssistance request;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visualisation);

        toolbar();
        findingLayoutElements();
        animations();
        getStringExtra();

        if (id != null || idInCharge != null || callerClass != null) {//se è uno dei 3 chiamanti HomeNeg, HomePos e taken
            initializeRequest(id, idInCharge); //Inizializzo il campo request dipendentemente dal fatto che ne abbia una già accettata da svolgere o meno
            setFields();

            if(idInCharge != null) { //se sono stato chiamato dalla taken
                String taken = "taken";

                menu.setOnClickListener(v -> {
                    animatedMenu(taken);
                });

                chat.setOnClickListener(v -> {
                    finish();
                });

                stop_helping.setOnClickListener(v -> {
                    stop_helping();
                });
            }

            if (callerClass != null && callerClass.equals("Homenegative")) { //se sono stato chiamato dalla HomeNeg
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

            if(callerClass != null && callerClass.equals("positive")) { //se sono stato chiamato dalla HomePos
                //Pulsanti visibili solo dall'utente positivo che richiede soccorso

                menu.setOnClickListener(v -> {
                    animatedMenu(callerClass);
                });

                deleteRequest.setOnClickListener(v -> {
                    delete_request();
                });

                chat.setOnClickListener(v -> {//TODO: forse da togliere
                    finish();
                });
            }

        }
        else { //altrimenti non c'è nulla da visualizzare
            new Utils.Dialog(this).show("Nessuna Incarico preso", "Nessuna richiesta è stata presa in carico!");
            String allGone = "allGone";
            animatedMenu(allGone);
        }
    }

    private void delete_request() {
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.show(getString(R.string.attention), getString(R.string.delete_request_warning));
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
                Thread t = new Thread(() -> {
                    User target = null;
                    try {
                        target = request.obtainRequestOwner();
                    } catch (ExecutionException | NoQuarantineAssistanceFoundException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendNotifications(target, "delete", CachedUser.user.getName());
                    user.removeQuarantineAssistance(request);
                    setResult(Activity.RESULT_OK);
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException ignored) {
                }
                /*Utils.executeAsync(() -> user.removeQuarantineAssistance(request), new Utils.TaskResult<Boolean>() { TODO: da vedere come farlo Async
                    @Override
                    public void onComplete(Boolean result) {

                        setResult(10);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                 */
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    private void accept_request() {
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.show(getString(R.string.information), getString(R.string.request_taken_in_charge));
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
                Utils.executeAsync(() -> request.updateInCharge_QuarantineAssistance(user), new Utils.TaskResult<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) throws ExecutionException, InterruptedException {
                        User target = request.obtainRequestOwner();
                        sendNotifications(target, "accept", target.getName());
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

    private void stop_helping() {
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.show(getString(R.string.attention), getString(R.string.stop_helping_request));
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
                Utils.executeAsync(() -> request.updateInCharge_QuarantineAssistance(null), new Utils.TaskResult<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) throws ExecutionException, InterruptedException {
                        User target = request.obtainRequestOwner();
                        sendNotifications(target, "stop", target.getName());
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

    private void setFields() {
        title = getIntent().getStringExtra("title");
        textTitle.setText(title);

        country = getIntent().getStringExtra("country");
        textCountry.setText(country);

        city = getIntent().getStringExtra("city");
        textCity.setText(city);

        if (request != null) {
            strDate = Utils.formatDateToString(request.getDeliveryDateToDate(), "kk:mm  dd/MM");
            textDate.setText(strDate);
            text.setText(request.getDescription());
        }
    }

    private void getStringExtra() {
        id = getIntent().getStringExtra("id");
        idInCharge = getIntent().getStringExtra("user");
        callerClass = getIntent().getStringExtra("class");
    }

    private void animations() {
        menuOpen = AnimationUtils.loadAnimation(this, R.anim.from_botton_animation);
        menuClose = AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation);
    }

    private void toolbar() {
        toolbar = findViewById(R.id.toolbar_RequestViz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Dettagli richiesta");
    }

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

    private void animatedMenu(@NonNull String caller) {
        if(caller.equals("positive")) {
            if (isOpen) {
                chat.startAnimation(menuClose);
                deleteRequest.startAnimation(menuClose);
                chat.setClickable(false);
                deleteRequest.setClickable(false);
                chat.setVisibility(View.GONE);
                deleteRequest.setVisibility(View.GONE);
                isOpen = false;
            } else {
                chat.setVisibility(View.VISIBLE);
                deleteRequest.setVisibility(View.VISIBLE);
                chat.startAnimation(menuOpen);
                deleteRequest.startAnimation(menuOpen);
                chat.setClickable(true);
                deleteRequest.setClickable(true);

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

    private void sendNotifications(User user, String event, String name) {
        switch (event) {
            case "accept":
                MessageService.sendMessageToUserDevices(user, MessageService.NotificationType.request_accepted,
                        "Richiesta presa in carico", name +" "+"ha preso in carico la tua richiesta di aiuto",
                        this);
            case "stop":
                MessageService.sendMessageToUserDevices(user, MessageService.NotificationType.request_stop_helping,
                        "Attenzione!", name + " "+"L'utente ha smesso di seguire la tua richiesta di aiuto",
                        this);
            case "delete":
                MessageService.sendMessageToUserDevices(user, MessageService.NotificationType.request_stop_helping,
                        "Attenzione!", name + " "+"Ha eliminato la richiesta, ti ringraziamo per la collaborazione",
                        this);
        }



    }
}
