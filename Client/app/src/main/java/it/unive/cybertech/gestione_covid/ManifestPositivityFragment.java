package it.unive.cybertech.gestione_covid;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import it.unive.cybertech.MainActivity;
import it.unive.cybertech.R;


public class ManifestPositivityFragment extends Fragment {


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
        TextView mDateSign = v.findViewById(R.id.textView_dateAlert2);
        TextView mStateSign = v.findViewById(R.id.textView_stateAlert2);
        Button signPosButton = v.findViewById(R.id.button_alertPos);


        /*TODO impostare parametri corretti nella HomePage gestione covid
          mNome.setText(ObjectClient.getName());
          mCognome.setText(ObjectClient.getSurname());
          if(ObjectClient.isPositive()){
                mDateSign.setText(ObjectClient.getDateSign());
                mStateSign("Positivo");
          }
          else{
                mDateSign.setText("Nessuna segnalazione inviata");
                mStateSign("Negativo");
          }
        */


        signPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ReportPositivityActivity.class));
            }
        });

    }
}