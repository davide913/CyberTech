package it.unive.cybertech.assistenza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.unive.cybertech.R;

/*
La home page di assistenza si occupa di mostrare le richieste in primo piano (in un qualche ordine)
 e di indirizzare l'utente verso le altre view a seconda se vuole creare una nuova richiesta, visualizzare
 quelle già create, andare sul suo profilo
 */
public class HomePage extends Fragment {
    List<RequestInfo> requestInfoList;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_assistenza, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.listRequests);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        requestInfoList = new ReferencedClass().getRequestInfoList();
        mAdapter = new ListAdapter(requestInfoList, getContext());
        recyclerView.setAdapter(mAdapter);


        //devo poter cliccare su ogni elemento della listRequest e visualizzare il layout request_visualisation
        recyclerView.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RequestViz.class));
        });

        //visibile solo se l'utente è positivo, quindi abilitato a chiedere aiuto alla community
        //qui estraggo lo user dal DB

        //il profilo è visibile da tutti
        view.findViewById(R.id.myProfile).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyProfile.class));
        });

        //Poter prendere in carico una richiesta solo se sei negativo
        view.findViewById(R.id.takenRequests).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TakenRequests.class));
        });

        //Devo creare l'utente e far vedere questo tasto solo se il flag positivity è true
        //My requests e newHelpRequest sono disponibili solo se sei positivo
        view.findViewById(R.id.buttMyRequests).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyRequests.class));
        });

        view.findViewById(R.id.newHelpRequest).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RequestDetails.class));
        });
    }
}