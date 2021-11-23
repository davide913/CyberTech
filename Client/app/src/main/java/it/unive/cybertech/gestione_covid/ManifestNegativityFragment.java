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
        Button button = v.findViewById(R.id.button_manifestNegativity);



        if(user.getPositiveSince() != null){
            imageView.setVisibility(View.INVISIBLE);
            nessunaSegnalazione.setVisibility(View.INVISIBLE);
            statoSegnalazione.setVisibility(View.VISIBLE);
            dataSegnalazione.setVisibility(View.VISIBLE);
            statoSegnalazione.setText("Positivo");
            dataSegnalazione.setText(CachedUser.user.getDatePositiveSince().toString());
            button.setVisibility(View.VISIBLE);
        }
        else{
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

}