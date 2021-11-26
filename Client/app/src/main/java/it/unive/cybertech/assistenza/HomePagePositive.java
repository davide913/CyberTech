package it.unive.cybertech.assistenza;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unive.cybertech.R;

public class HomePagePositive extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_positive, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        //Devo creare l'utente e far vedere questo tasto solo se il flag positivity è true
        //My requests e newHelpRequest sono disponibili solo se sei positivo
        view.findViewById(R.id.buttMyRequest).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyRequests.class));
        });

        view.findViewById(R.id.newHelpRequest).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RequestDetails.class));
        });

        /*
        view.findViewById(R.id.btn_chat).setOnClickListener(v -> {
            );
        });
         */
    }
}