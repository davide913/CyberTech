package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.getQuarantineAssistanceById;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;

public class RequestViz extends AppCompatActivity {
    User user = CachedUser.user;
    FloatingActionButton menu, chat, deleteRequest, accept_request, stop_helping;
    Animation menuOpen, menuClose;
    boolean isOpen = false;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visualisation);

        Toolbar toolbar = findViewById(R.id.toolbar_RequestViz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Dettagli richiesta");

        final QuarantineAssistance[] request = {null};
        String id = getIntent().getStringExtra("id");
        String idInCharge = getIntent().getStringExtra("user");
        String callerClass = getIntent().getStringExtra("class");

        menu = (FloatingActionButton) findViewById(R.id.menu_open);
        chat = (FloatingActionButton) findViewById(R.id.chat_from_request);
        accept_request = (FloatingActionButton) findViewById(R.id.acceptRequest);
        stop_helping = (FloatingActionButton) findViewById(R.id.stopHelping);
        deleteRequest = (FloatingActionButton) findViewById(R.id.deleteRequest);

        //Animations
        menuOpen = AnimationUtils.loadAnimation(this, R.anim.from_botton_animation);
        menuClose = AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation);

        if (id != null || idInCharge != null || callerClass != null) {//se è uno dei 3 chiamanti HomeNeg, HomePos e taken
            if(id != null || idInCharge != null) {
                Thread t = new Thread(() -> {
                    try {
                        request[0] = getQuarantineAssistanceById(idInCharge == null ? id : idInCharge);
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
            }
            TextView textTitle = findViewById(R.id.textTitle);
            TextView text = findViewById(R.id.textFull);
            TextView textCountry = findViewById(R.id.textCountry);
            TextView textCity = findViewById(R.id.textCity);
            TextView textDate = findViewById(R.id.textDate);

            String title = getIntent().getStringExtra("title");
            textTitle.setText(title);

            String country = getIntent().getStringExtra("country");
            textCountry.setText(country);

            String city = getIntent().getStringExtra("city");
            textCity.setText(city);

            if (request[0] != null)
                text.setText(request[0].getDescription());
            else
                text.setText("Nessun testo è stato inserito");

            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm  dd-MM");
            if (request[0] != null) {
                //Date date = request[0].getDateDeliveryToDate();
                //String strDate = dateFormat.format(date);
                //textDate.setText(strDate);
            } else
                textDate.setText("NAN");


            if(idInCharge != null) { //se sono stato chiamato dalla taken
                QuarantineAssistance thisRequest = request[0];
                String taken = "taken";

                menu.setOnClickListener(v -> {
                    animatedMenu(taken);
                });

                chat.setOnClickListener(v -> {
                    finish();
                });

                stop_helping.setOnClickListener(v -> {
                    Utils.Dialog dialog = new Utils.Dialog(this);
                    dialog.show("Attenzione!", "Stai per abbandonare la richiesta");
                    dialog.setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            Thread t = new Thread(() -> {
                                thisRequest.updateInCharge_QuarantineAssistance(null);
                                setResult(Activity.RESULT_OK);
                            });
                            t.start();
                            try {
                                t.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            finish();
                        }

                        @Override
                        public void onCancel() {
                            finish();
                        }
                    });
                });
            }

            if (callerClass != null && callerClass.equals("Homenegative")) { //se sono stato chiamato dalla HomeNeg
                String idTaken = getIntent().getStringExtra("alreadyTaken");

                if(idTaken == null) {
                    QuarantineAssistance finalRequest1 = request[0];

                    menu.setOnClickListener(v -> {
                        animatedMenu(callerClass);
                    });

                    accept_request.setOnClickListener(v -> {
                        //TODO: manca da fare il controllo sul poter accettare solo una richiesta alla volta

                        Utils.Dialog dialog = new Utils.Dialog(this);
                        dialog.show("Operazione confermata!", "Hai preso in carico una richiesta");
                        dialog.setCallback(new Utils.DialogResult() {
                            @Override
                            public void onSuccess() {
                                Thread t = new Thread(() -> {
                                    finalRequest1.updateInCharge_QuarantineAssistance(user);
                                    setResult(Activity.RESULT_OK);
                                });
                                t.start();
                                try {
                                    t.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }

                            @Override
                            public void onCancel() {
                                finish();
                            }
                        });
                    });
                }
                else
                    menu.setVisibility(View.GONE);
            }

            if(callerClass != null && callerClass.equals("positive")) { //se sono stato chiamato dalla HomePos
                //Pulsanti visibili solo dall'utente positivo che richiede soccorso
                QuarantineAssistance finalRequest2 = request[0];
/*
                menu.setOnClickListener(v -> {
                    animatedMenu(callerClass);
                });

                deleteRequest.setOnClickListener(v -> {
                    Utils.Dialog dialog = new Utils.Dialog(this);
                    dialog.show("Attenzione!", "Vuoi davvero eliminare la richiesta?");
                    dialog.setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            Thread t = new Thread(() -> {
                                try {
                                    finalRequest2.removeQuarantineAssistance();
                                    setResult(Activity.RESULT_OK);
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
                            finish();
                        }

                        @Override
                        public void onCancel() {
                            finish();
                        }
                    });
                });

 */

                chat.setOnClickListener(v -> {
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

    private void animatedMenu(String caller) {
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
}
