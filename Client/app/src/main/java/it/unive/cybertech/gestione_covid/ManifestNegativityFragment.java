package it.unive.cybertech.gestione_covid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.User;
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




        if(CachedUser.user.getDatePositiveSince() != null){
            imageView.setVisibility(View.INVISIBLE);
            nessunaSegnalazione.setVisibility(View.INVISIBLE);
            statoSegnalazione.setVisibility(View.VISIBLE);
            dataSegnalazione.setVisibility(View.VISIBLE);
            statoSegnalazione.setText("Positivo");
            dataSegnalazione.setText(CachedUser.user.getDatePositiveSince().toString());
        }
        else{
            imageView.setVisibility(View.VISIBLE);
            nessunaSegnalazione.setVisibility(View.VISIBLE);
            statoSegnalazione.setVisibility(View.INVISIBLE);
            dataSegnalazione.setVisibility(View.INVISIBLE);
        }




    }


}