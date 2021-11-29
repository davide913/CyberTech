package it.unive.cybertech.gestione_covid;

import android.content.DialogInterface;
import android.os.Bundle;

import static it.unive.cybertech.utils.CachedUser.user;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.utils.CachedUser;


public class ManifestNegativityFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manifest_negativity, container, false);

        initViews(v);

        return v;
    }


    private void initViews(View v){
        ImageView imageView = v.findViewById(R.id.imageView_noSign);
        TextView statoSegnalazione = v.findViewById(R.id.textView_stato);
        TextView dataSegnalazione = v.findViewById(R.id.textView_data);
        TextView nessunaSegnalazione = v.findViewById(R.id.textView_noSignEff);
        TextView statosegnalazionebordi = v.findViewById(R.id.textView_statoSegnalazione);
        TextView datasegnalazionebordi = v.findViewById(R.id.textView_dataSegnalazione2);
        Button button = v.findViewById(R.id.button_manifestNegativity);



        if(user.getPositiveSince() != null){
            imageView.setVisibility(View.INVISIBLE);
            nessunaSegnalazione.setVisibility(View.INVISIBLE);
            statoSegnalazione.setVisibility(View.VISIBLE);
            dataSegnalazione.setVisibility(View.VISIBLE);
            statosegnalazionebordi.setVisibility(View.VISIBLE);
            datasegnalazionebordi.setVisibility(View.VISIBLE);
            dataSegnalazione.setText("Positivo");
            statoSegnalazione.setText(convertDate(CachedUser.user.getPositiveSince().toString()));
            button.setVisibility(View.VISIBLE);
        }
        else{
            statosegnalazionebordi.setVisibility(View.INVISIBLE);
            datasegnalazionebordi.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            nessunaSegnalazione.setVisibility(View.VISIBLE);
            statoSegnalazione.setVisibility(View.INVISIBLE);
            dataSegnalazione.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
        }

        button.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Invia Guarigione");
            builder.setMessage("Confermi di voler inviare la segnalazione di guarigione?\n");
            builder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateDateOnDb();
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


    }


    private void updateFr(){  //Permette di aggiornare i fragments
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.gestione_covid.HomePage()).commit();
    }

    private void updateDateOnDb(){
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
        char[] charanno = {date.charAt(24),date.charAt(25),date.charAt(26),date.charAt(26)};
        String anno = new String(charanno);

        for (int i = 0; i<12; i++){
            if (mese.equals(mesi.get(i))){
                mese = String.valueOf(i+1);
                break;
            }
        }


        return giorno + "/" + mese + "/" + anno;
    }

}