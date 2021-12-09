package it.unive.cybertech.assistenza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;
import it.unive.cybertech.database.Profile.QuarantineAssistance;

/*
La home page di assistenza si occupa di mostrare le richieste in primo piano (in un qualche ordine)
 e di indirizzare l'utente verso le altre view a seconda se vuole creare una nuova richiesta, visualizzare
 quelle già create, andare sul suo profilo
 */
public class HomePageNegative extends Fragment {
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_assistenza, container, false);
        initViews(view);
        //TODO use CachedUser to work with database
        return view;
    }

    private void initViews(View view) {
        ArrayAdapter<QuarantineAssistance> adapter;
        ArrayList<QuarantineAssistance> myQuarantineList = new ArrayList<QuarantineAssistance>();
        listView = view.findViewById(R.id.listRequests);
        adapter = new CastomRequestsAdapter(getContext(), 0, myQuarantineList);
        listView.setAdapter(adapter);

        final String[] homeType = new String[1];


        // Get reference of widgets from XML layout
        Spinner myHomeSpinner = view.findViewById(R.id.homeNegSpinner);

        // Initializing a String Array
        String[] typeOptions = new String[]{
                "Tutte le categorie...",
                "Medicinali",
                "Spesa",
                "Commissioni",
                "Posta"
        };

        final List<String> typeList = new ArrayList<>(Arrays.asList(typeOptions));

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, typeList){

            @Override
            public boolean isEnabled(int position){
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        myHomeSpinner.setAdapter(spinnerArrayAdapter);

        myHomeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                homeType[0] = selectedItemText;
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        listView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);
            newIntent.putExtra("title",  adapter.getItem(position).getTitle());
            newIntent.putExtra("location", adapter.getItem(position).getLocation().toString());
            newIntent.putExtra("date", adapter.getItem(position).getDateDeliveryDate().toString());

            //
            newIntent.putExtra("class", "negative");
            startActivity(newIntent);
        }));

        //Poter prendere in carico una richiesta solo se sei negativo
        view.findViewById(R.id.takenRequest).setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);

            newIntent.putExtra("class", "negative"); //TODO: da vedere se c'è bisogno di passare altre info per identificare chi sta chiedendo la proprie richiesta presa in carico
            startActivity(newIntent);
        });


        view.findViewById(R.id.btn_chatNeg).setOnClickListener(v -> {
            //ci metto il collegamento alla chat
        });

    }
}