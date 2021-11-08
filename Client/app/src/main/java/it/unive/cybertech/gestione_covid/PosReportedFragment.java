package it.unive.cybertech.gestione_covid;

import androidx.fragment.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.unive.cybertech.R;
import it.unive.cybertech.gestione_covid.adapters.CustomSignReceivedAdapter;

public class PosReportedFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pos_reported, container, false);

        initViews(v);

        return v;
    }

    private void initViews(View v){
        Boolean var = false;

        ImageView imageView = v.findViewById(R.id.imageView_PosReported);
        TextView textView = v.findViewById(R.id.TextView_PosReported);
        TextView textView1 = v.findViewById(R.id.textView_UltimeSegnalazioni);
        ListView listView = v.findViewById(R.id.ListView_signReported);

        if(var){
            imageView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            ArrayList<String> sign = new ArrayList<>();
            ArrayAdapter<String> adapter;

            //Prove ListView con Adapter
            sign.add("rawlist1");
            sign.add("rawlist2");
            sign.add("rawlist3");
            sign.add("rawlist4");
            sign.add("rawlist5");
            sign.add("rawlist6");

            adapter = new CustomSignReceivedAdapter(getContext(), 0, sign);

            listView.setAdapter(adapter);
        }
        else{
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            textView1.setVisibility(View.INVISIBLE);
        }


    }

}