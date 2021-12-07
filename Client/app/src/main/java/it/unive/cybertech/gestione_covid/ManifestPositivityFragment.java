package it.unive.cybertech.gestione_covid;

import static it.unive.cybertech.utils.CachedUser.user;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.unive.cybertech.MainActivity;
import it.unive.cybertech.R;


public class ManifestPositivityFragment extends Fragment {
    final Calendar myCalendar = Calendar.getInstance();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manifest_positivity, container, false);

        initViews(v);

        return v;
    }


    private void initViews(View v){

        TextView mNome = v.findViewById(R.id.textView_nome2);
        TextView mCognome = v.findViewById(R.id.textView_cognome2);
        EditText mDateSign = v.findViewById(R.id.textView_dateAlert2);
        TextView mStateSign = v.findViewById(R.id.textView_stateAlert2);
        Button signPosButton = v.findViewById(R.id.button_alertPos);
        Button bManifestNegativity = v.findViewById(R.id.button_signGua);


        mNome.setText(user.getName());
          mCognome.setText(user.getSurname());

          if(user.getPositiveSince() != null){
                mDateSign.setHint(convertDate(user.getPositiveSince().toDate().toString()));
                mStateSign.setText("Positivo");
                signPosButton.setVisibility(View.INVISIBLE);
                bManifestNegativity.setVisibility(View.VISIBLE);
          }
          else{
                mDateSign.setHint("Nessuna segnalazione inviata");
                mStateSign.setText("Negativo");
              signPosButton.setVisibility(View.VISIBLE);
              bManifestNegativity.setVisibility(View.INVISIBLE);
          }

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(v);
            }

        };

        mDateSign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });





          bManifestNegativity.setOnClickListener(v1 -> {
              AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
              builder.setTitle("Invia Guarigione");
              builder.setMessage("Confermi di voler inviare la segnalazione di guarigione?\n");
              builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      Thread t = new Thread(new Runnable() {
                          @Override
                          public void run() {
                              user.updatePositiveSince(null); //Imposta la data a Null sul database
                          }
                      });
                      t.start();
                      try {
                          t.join();                           //Aspetta che il thread abbia finito prima di riaggiornare i fragments
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                      updateFr();
                      dialog.cancel();
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





        signPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Inviare Segnalazione?");
                builder.setMessage("Sei sicuro di voler inviare la segnalazione?\n");
                builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String data = mDateSign.getText().toString();
                        try{
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // here set the pattern as you date in string was containing like date/month/year
                            Date d = sdf.parse(data);
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    user.updatePositiveSince(d); //TODO vedere se funziona
                                }
                            });
                            t.start();
                            t.join();
                        }catch(ParseException | InterruptedException ex){
                            // handle parsing exception if date string was different from the pattern applying into the SimpleDateFormat contructor
                        }
                        updateFr();
                        dialog.cancel();
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
        });


    }

    private void updateLabel(View v) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText selectDate = v.findViewById(R.id.textView_dateAlert2);

        selectDate.setText(sdf.format(myCalendar.getTime()));
    }


    private void updateFr(){  //Permette di aggiornare i fragments
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.gestione_covid.HomePage()).commit();
    }


    private String convertDate(String date){
        ArrayList<String> mesi = new ArrayList<>();
        mesi.add("Jan");
        mesi.add("Feb");
        mesi.add("Mar");
        mesi.add("Apr");
        mesi.add("May");
        mesi.add("Jun");
        mesi.add("Jul");
        mesi.add("Aug");
        mesi.add("Sep");
        mesi.add("Oct");
        mesi.add("Nov");
        mesi.add("Dec");
        ArrayList<Integer> nmesi = new ArrayList<>();
        for(int i = 0; i<12; i++){
            nmesi.add(i);
        }
        char[] charmese = {date.charAt(4),date.charAt(5),date.charAt(6)};
        String mese = new String(charmese);
        char[] chargiorno = {date.charAt(8),date.charAt(9)};
        String giorno = new String(chargiorno);
        char[] charanno = {date.charAt(24),date.charAt(25)};
        String anno = new String(charanno);
        anno = "20"+anno;

        for (int i = 0; i<12; i++){
            if (mese.equals(mesi.get(i))){
                mese = String.valueOf(i+1);
                break;
            }
        }


        return giorno + "/" + mese + "/" + anno;
    }



}