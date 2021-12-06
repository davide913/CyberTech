package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;
import it.unive.cybertech.database.Profile.QuarantineAssistance;

public class HomePagePositive extends Fragment {
    ListView listTakenView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_positive, container, false);
        initView(view);

        return view;
    }

//Modificato 29/11: Aggiunta ListView per mostrare solo le richieste di aiuto caricate da me
    private void initView(View view) {
        ArrayList<QuarantineAssistance> takenRequestsList = new ArrayList<>();
        listTakenView = view.findViewById(R.id.lst_myRequests);
        ArrayAdapter<QuarantineAssistance> adapter;

        //takenRequestsList.add("La prova nella seconda home");

        adapter = new CastomRequestsAdapter(getContext(), 0, takenRequestsList);
        listTakenView.setAdapter(adapter);

        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        listTakenView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class); //Qui dal lato Positivo devo poter modificare la richiesta oppure eliminarla

            //i seguenti campi andranno compilati con le info
            newIntent.putExtra("title", adapter.getItem(position).getTitle());
            newIntent.putExtra("location", adapter.getItem(position).getLocation().toString());
            newIntent.putExtra("date", adapter.getItem(position).getDateDeliveryDate().toString());


            newIntent.putExtra("class", "positive"); //per indicare se il chiamante è la HomePositive o Negative
            startActivity(newIntent);
        }));

        view.findViewById(R.id.newHelpRequest).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RequestDetails.class));
        });
    }
}