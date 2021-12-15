package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.getQuarantineAssistanceByInCharge;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.utils.CachedUser;

public class HomePagePositive extends Fragment {
    ListView listTakenView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page_positive, container, false);
        try {
            initView(view);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View view) throws ExecutionException, InterruptedException {
        ArrayList<QuarantineAssistance> myRequestsList;
        listTakenView = view.findViewById(R.id.lst_myRequests);
        ArrayAdapter<QuarantineAssistance> adapter;
        myRequestsList = RequestDetails.myRequests;
        //TODO: fare i test per vedere se myRequestsList contiene le richieste fatte fin'ora

        adapter = new CastomRequestsAdapter(getContext(), 0, myRequestsList);
        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        listTakenView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class); //Qui dal lato Positivo devo poter modificare la richiesta oppure eliminarla

            newIntent.putExtra("title", adapter.getItem(position).getTitle());
            newIntent.putExtra("location", adapter.getItem(position).getLocation().toString());
            newIntent.putExtra("date", adapter.getItem(position).getDateDeliveryToDate().toString());
            newIntent.putExtra("id", adapter.getItem(position).getId());


            newIntent.putExtra("class", "positive"); //per indicare se il chiamante è la HomePositive o Negative
            startActivity(newIntent);
        }));

        view.findViewById(R.id.newHelpRequest).setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(), RequestDetails.class);

            newIntent.putExtra("changeFields", "true");
            startActivity(newIntent);
        });
    }

}
