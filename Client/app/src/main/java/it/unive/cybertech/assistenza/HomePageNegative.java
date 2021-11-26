package it.unive.cybertech.assistenza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static it.unive.cybertech.utils.CachedUser.user;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;

/*
La home page di assistenza si occupa di mostrare le richieste in primo piano (in un qualche ordine)
 e di indirizzare l'utente verso le altre view a seconda se vuole creare una nuova richiesta, visualizzare
 quelle già create, andare sul suo profilo
 */
public class HomePageNegative extends Fragment {
    private ArrayList<String> requestInfoList;
    private ArrayList<QuarantineAssistance> myQuarantineList;
    ListView listView;

    ListView myNewListView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_assistenza, container, false);
        initViews(view);
        //TODO use CachedUser to work with database
        return view;
    }

    private void initViews(View view) {
        requestInfoList = new ArrayList<String>();
        ArrayAdapter<String> adapter; //TODO: non più String, ma QuarantineAssistance

        listView = view.findViewById(R.id.listRequests);
        requestInfoList.add("stringa di prova");
        requestInfoList.add("La seconda prova della Home");

        adapter = new CastomRequestsAdapter(getContext(), 0, requestInfoList); // myQuarantineList
        listView.setAdapter(adapter);

        /*
        myQuarantineList = new ArrayList<QuarantineAssistance>();
        ArrayAdapter<QuarantineAssistance> myAdapter;
        myNewListView = view.findViewById(R.id.listRequests);

        myAdapter = new CastomRequestsAdapter(getContext(), 0, myQuarantineList, 0);
        myNewListView.setAdapter(myAdapter);
        */

        //TODO mettere un spinner per un ulteriore filtro di ricerca

        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        listView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);
            newIntent.putExtra("title", adapter.getItem(position));
            newIntent.putExtra("location", adapter.getItem(position));
            startActivity(newIntent);
        }));

        //Poter prendere in carico una richiesta solo se sei negativo
        view.findViewById(R.id.takenRequests).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TakenRequests.class));
        });

        /*
        view.findViewById(R.id.btn_chatNeg).setOnClickListener(v -> {
            ci metto il collegamento alla chat);
        });

         */

    }
}