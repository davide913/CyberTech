package it.unive.cybertech.gestione_covid;

import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Showables.showShortToast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.MainActivity;
import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.messages.MessageService;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;

    /**
     * ManifestPositivityFragment is the main fragment displayed when entering the Covid-19 section.
     * This Fragment contains the code that allows a user to send a report to other users.
     *
     * @author Enrico De Zorzi
     * @since 1.0
     */

public class ManifestPositivityFragment extends Fragment {
    final Calendar myCalendar = Calendar.getInstance();
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manifest_positivity, container, false);
        context = getContext();
        initViews(v);

        return v;
    }

    /**
     * InitViews initializes the screen.
     * Set the correct values in the various fields and calls up methods when buttons are touched.
     *
     * @author Enrico De Zorzi
     * @since 1.0
     */
    private void initViews(View v) {  //Configure the screen

        TextView mNome = v.findViewById(R.id.textView_nome2);
        TextView mCognome = v.findViewById(R.id.textView_cognome2);
        EditText mDateSign = v.findViewById(R.id.textView_dateAlert2);
        TextView mStateSign = v.findViewById(R.id.textView_stateAlert2);
        Button signPosButton = v.findViewById(R.id.button_alertPos);
        Button bManifestNegativity = v.findViewById(R.id.button_signGua);


        mNome.setText(user.getName());
        mCognome.setText(user.getSurname());

        if (user.getPositiveSince() != null) {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            mDateSign.setHint(sdf.format(user.getPositiveSince().toDate()));
            mStateSign.setText("Positivo");
            signPosButton.setVisibility(View.INVISIBLE);
            bManifestNegativity.setVisibility(View.VISIBLE);
        } else {
            mDateSign.setHint("No Date");
            mStateSign.setText("Negativo");
            signPosButton.setVisibility(View.VISIBLE);
            bManifestNegativity.setVisibility(View.INVISIBLE);
        }

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {


            /**
             * onDateSet allows you to select a date from a DatePicker.
             *
             * @author Enrico De Zorzi
             * @since 1.3
             */
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(v);
            }

        };


        if (mDateSign.getHint().equals("No Date")) {

            mDateSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        }


        /**
         * When the button is clicked to report healing from Covid:
         * - The correct parameters are set in the fields;
         * -The user who sent the healing is removed from positive users.
         *
         * @author Enrico De Zorzi
         * @since 1.2
         */
        bManifestNegativity.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Invia Guarigione");
            builder.setMessage("Confermi di voler inviare la segnalazione di guarigione?\n");
            builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Utils.executeAsync(() -> user.updatePositiveSince(null), new Utils.TaskResult<Boolean>() {
                        @Override
                        public void onComplete(Boolean result) {
                            updateFr();
                            dialog.cancel();

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }


            });
            builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        });


        /**
         * When the button is pressed to report positivity to Covid-19:
         * - The correct parameters are set in the fields;
         * - The user is added to the list of positive users;
         * - A notification is sent to all users in his own activity.
         *
         * @author Enrico De Zorzi
         * @since 1.2
         */
        signPosButton.setOnClickListener(new View.OnClickListener() { //events that occur when the button is pressed
            @Override
            public void onClick(View v) {
                String data = mDateSign.getHint().toString();
                if (!data.equals("No Date")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Inviare Segnalazione?");
                    builder.setMessage("Sei sicuro di voler inviare la segnalazione?\n");
                    builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String data = mDateSign.getHint().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // here set the pattern as you date in string was containing like date/month/year
                            Date d = null;
                            try {
                                d = sdf.parse(data);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date finalD = d;
                            Utils.executeAsync(() -> user.updatePositiveSince(finalD), new Utils.TaskResult<Boolean>() {
                                @Override
                                public void onComplete(Boolean result) {
                                    Utils.executeAsync(() -> user.obtainActivitiesUsers(), new Utils.TaskResult<Collection<User>>() {
                                        @Override
                                        public void onComplete(Collection<User> result) {
                                            sendNotifications(result);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });
                                    updateFr();
                                    dialog.cancel();

                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }

                    });

                    builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();

                }
                else{
                    Toast errorToast = Toast.makeText(getActivity(), "Devi inserire una data!", Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });


    }

        /**
         * updateLabel visually changes the displayed date and sets it to the correct format.
         *
         * @author Enrico De Zorzi
         * @since 1.3
         */
    private void updateLabel(View v) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText selectDate = v.findViewById(R.id.textView_dateAlert2);

        selectDate.setHint(sdf.format(myCalendar.getTime()));
    }

        /**
         * updateFr allows you to update all Fragments of the Covid-19 section.
         *
         * @author Enrico De Zorzi
         * @since 1.2
         */
    private void updateFr() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.gestione_covid.HomePage()).commit();
    }

        /**
         * sendNotification is the function that given a Collection
         * sends the notification to the devices of all users in the Collection
         *
         * @author Enrico De Zorzi
         * @since 1.4
         */
    private void sendNotifications(Collection<User> users) {
        for (User u : users) {
            MessageService.sendMessageToUserDevices(u, MessageService.NotificationType.coronavirus,
                    "ATTENZIONE: Utente positivo", "Un utente presente nelle tue attività è risultato positivo",
                    context);
        }

    }

}