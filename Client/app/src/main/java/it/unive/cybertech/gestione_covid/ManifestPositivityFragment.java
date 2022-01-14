package it.unive.cybertech.gestione_covid;

import static it.unive.cybertech.utils.CachedUser.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.messages.MessageService;
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
        initViews(v);

        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
            new Utils.Dialog(getContext(), true, getString(R.string.send), true, "Annulla")
                    .setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            Utils.executeAsync(() -> user.updatePositiveSince(null), new Utils.TaskResult<Boolean>() {
                                @Override
                                public void onComplete(Boolean result) {
                                    updateFr();
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }

                        @Override
                        public void onCancel() {

                        }
                    })
                    .show(getString(R.string.send_healing), getString(R.string.confirm_send_healing));
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
                    new Utils.Dialog(getContext(), true, getString(R.string.send), true, "Annulla")
                            .setCallback(new Utils.DialogResult() {
                                @Override
                                public void onSuccess() {
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
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancel() {

                                }
                            })
                            .show(getString(R.string.send_report), getString(R.string.confirm_sent_report));

                } else {
                    Toast errorToast = Toast.makeText(getActivity(), R.string.insert_a_date, Toast.LENGTH_SHORT);
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
        EditText selectDate = v.findViewById(R.id.textView_dateAlert2);
        selectDate.setHint(Utils.formatDateToString(myCalendar.getTime()));
    }

    /**
     * updateFr allows you to update all Fragments of the Covid-19 section.
     *
     * @author Enrico De Zorzi
     * @since 1.2
     */
    private void updateFr() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
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
                    getString(R.string.attention_user_positive), getString(R.string.positive_user_in_activity),
                    context);
        }

    }

}