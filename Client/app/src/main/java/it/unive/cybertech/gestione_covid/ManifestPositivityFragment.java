package it.unive.cybertech.gestione_covid;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import it.unive.cybertech.MainActivity;
import it.unive.cybertech.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManifestPositivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManifestPositivityFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManifestPositivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManifestPositivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManifestPositivityFragment newInstance(String param1, String param2) {
        ManifestPositivityFragment fragment = new ManifestPositivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initViews();
    }

    private void initViews(){


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manifest_positivity, container, false);

        TextView mNome = v.findViewById(R.id.textView_nome2);
        TextView mCognome = v.findViewById(R.id.textView_cognome2);
        TextView mDateSign = v.findViewById(R.id.textView_dateAlert2);
        TextView mStateSign = v.findViewById(R.id.textView_stateAlert2);
        Button signPosButton = v.findViewById(R.id.button_alertPos);

        signPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ReportPositivityActivity.class));
            }
        });

        return v;
    }
}