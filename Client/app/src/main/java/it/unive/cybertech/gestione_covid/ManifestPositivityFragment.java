package it.unive.cybertech.gestione_covid;

import static it.unive.cybertech.utils.CachedUser.user;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import it.unive.cybertech.MainActivity;
import it.unive.cybertech.R;


public class ManifestPositivityFragment extends Fragment {
    public static String tag = "android:switcher:"+R.id.viewPager_covid+":"+1;

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



          mNome.setText(user.getName());
          mCognome.setText(user.getSurname());

          if(user.getPositiveSince() != null){
                mDateSign.setText((user.getDatePositiveSince().toString()));
                mStateSign.setText("Positivo");
          }
          else{
                mDateSign.setText("Nessuna segnalazione inviata");
                mStateSign.setText("Negativo");
          }





        signPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportPositivityActivity.class);
                startActivityForResult(intent, 10001);

            }
        });



    }

    private void updateFr(){  //Permette di aggiornare i fragments
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.gestione_covid.HomePage()).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK))
            // recreate your fragment here
            updateFr();
    }

    private String convertDate(String date){ //TODO vedere se funziona
        String datafinale = "";
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
        for(int i = 1; i<=12; i++){
            nmesi.add(i);
        }
        char[] charmese = {date.charAt(4),date.charAt(5),date.charAt(6)};
        String mese = new String(charmese);

        return datafinale;
    }

}