package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.getQuarantineAssistanceById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        if (id != null || idInCharge != null || callerClass != null) {//se è uno dei 3 chiamanti HomeNeg, HomePos e taken
            if(id != null || idInCharge != null) {
                Thread t = new Thread(() -> {
                    QuarantineAssistance request2 = null;
                    try {
                        request2 = getQuarantineAssistanceById(idInCharge == null ? id : idInCharge);
                        request[0] = request2;
                        Log.d("Richiesta presa nella RequestViz", request2.getId());
                        Log.d("La prova", request[0].getId());
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
            TextView textAddress = findViewById(R.id.textAddress);
            TextView textDate = findViewById(R.id.textDate);

            String title = getIntent().getStringExtra("title");
            textTitle.setText(title);

            String country = getIntent().getStringExtra("country");
            textCountry.setText(country);

            String city = getIntent().getStringExtra("city");
            textCity.setText(city);

            String address = getIntent().getStringExtra("address");
            textAddress.setText(address);

            if (request[0] != null)
                text.setText(request[0].getDescription());
            else
                text.setText("Nessun testo è stato inserito");

            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (request[0] != null) {
                //Date date = Calendar.getInstance().getTime();
                String strDate = request[0].getDeliveryDate().toString();
                textDate.setText(strDate);
            } else
                textDate.setText("NAN");


            if(idInCharge != null) { //se sono stato chiamato dalla taken
                this.findViewById(R.id.btn_chat).setOnClickListener(v -> { //per spostarsi alla chat locale con chi ha accettato la richiesta
                    finish();
                });

                QuarantineAssistance thisRequest = request[0];
                this.findViewById(R.id.stop_helping).setOnClickListener(v -> { //TODO: sistemare stop_helping e accept
                    Utils.Dialog dialog = new Utils.Dialog(this);
                    dialog.show("Attenzione!", "Stai per abbandonare la richiesta");
                    dialog.setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            Thread t = new Thread(() -> {
                                thisRequest.updateInCharge_QuarantineAssistance(null);
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

                this.findViewById(R.id.btn_changeFields).setVisibility(View.GONE);
                this.findViewById(R.id.btn_deleteRequest).setVisibility(View.GONE);
                this.findViewById(R.id.accept_request).setVisibility(View.GONE);
            }

            if (callerClass != null && callerClass.equals("Homenegative")) { //se sono stato chiamato dalla HomeNeg
                    QuarantineAssistance finalRequest1 = request[0];
                    this.findViewById(R.id.accept_request).setOnClickListener(v -> {
                        //La richiesta viene affidata a me, la posso visualizzare nella mia sezione Taken Requests e tolta dalla lista nella Home

                        Utils.Dialog dialog = new Utils.Dialog(this);
                        dialog.show("Operazione confermata!", "Hai preso in carico una richiesta");
                        dialog.setCallback(new Utils.DialogResult() {
                            @Override
                            public void onSuccess() {
                                Thread t = new Thread(() -> {
                                    finalRequest1.updateInCharge_QuarantineAssistance(user);
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

                    this.findViewById(R.id.btn_changeFields).setVisibility(View.GONE);
                    this.findViewById(R.id.stop_helping).setVisibility(View.GONE);
                    this.findViewById(R.id.btn_deleteRequest).setVisibility(View.GONE);
                    this.findViewById(R.id.btn_chat).setVisibility(View.GONE);
            }
            if(callerClass != null && callerClass.equals("positive")) { //se sono stato chiamato dalla HomePos
                //Pulsanti visibili solo dall'utente positivo che richiede soccorso
                QuarantineAssistance finalRequest = request[0];
                this.findViewById(R.id.btn_changeFields).setOnClickListener(v -> { //per modificare i campi, nel DB verificare e sostituire i campi nuovi con quelli vecchi della stessa richiesta
                    Intent newIntent = new Intent(this, RequestDetails.class);

                    newIntent.putExtra("id", finalRequest.getId());
                    startActivity(newIntent);
                    finish();
                });

                QuarantineAssistance finalRequest2 = request[0];
                this.findViewById(R.id.btn_deleteRequest).setOnClickListener(v -> { //per eliminare dal Db la richiesta
                    Thread m = new Thread(() -> {
                        try {
                            finalRequest2.removeQuarantineAssistance();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    m.start();
                    try {
                        m.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                });

                this.findViewById(R.id.btn_chat).setOnClickListener(v -> { //per spostarsi alla chat locale con chi ha accettato la richiesta
                    finish();
                });

                this.findViewById(R.id.accept_request).setVisibility(View.GONE);
                this.findViewById(R.id.stop_helping).setVisibility(View.GONE);
            }

        }
        else { //altrimenti non c'è nulla da visualizzare
            new Utils.Dialog(this).show("Nessuna Incarico preso", "Nessuna richiesta è stata presa in carico!");

            this.findViewById(R.id.btn_changeFields).setVisibility(View.GONE);
            this.findViewById(R.id.btn_deleteRequest).setVisibility(View.GONE);
            this.findViewById(R.id.accept_request).setVisibility(View.GONE);
            this.findViewById(R.id.btn_chat).setVisibility(View.GONE);
            this.findViewById(R.id.stop_helping).setVisibility(View.GONE);

            this.findViewById(R.id.textTitle).setVisibility(View.GONE);
            this.findViewById(R.id.textFull).setVisibility(View.GONE);
            this.findViewById(R.id.textCountry).setVisibility(View.GONE);
            this.findViewById(R.id.textCity).setVisibility(View.GONE);
            this.findViewById(R.id.textAddress).setVisibility(View.GONE);
            this.findViewById(R.id.textDate).setVisibility(View.GONE);
        }
    }

}
