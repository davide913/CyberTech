package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.getQuarantineAssistanceByInCharge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;

public class HomePageNegative extends Fragment {
    ListView listView;
    User user = CachedUser.user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page_assistenza, container, false);

        try {
            initViews(view);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //TODO use CachedUser to work with database
        return view;
    }

    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void initViews(View view) throws ExecutionException, InterruptedException {
        ArrayAdapter<QuarantineAssistance> adapter;
        ArrayList<QuarantineAssistance> myQuarantineList = new ArrayList<>();
        listView = view.findViewById(R.id.listRequests);

        final ArrayList<AssistanceType> adapterList = new ArrayList<>();
        final QuarantineAssistance[] inCharge = {null};
        final String[] homeType = new String[1];

        Spinner sp = view.findViewById(R.id.homeNegSpinner);
        ArrayList<String> names = new ArrayList<>();

        Thread t = new Thread(() -> {
            ArrayList<AssistanceType> tList = null;
            ArrayList<QuarantineAssistance> myQuar = null;
            try {
                tList = AssistanceType.getAssistanceTypes();
                myQuar = QuarantineAssistance.getJoinableQuarantineAssistance(null, null, 10);
                inCharge[0] = getQuarantineAssistanceByInCharge(user);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            for (AssistanceType a: tList) {
                names.add(a.getType());
                adapterList.add(a);
            }
            for (QuarantineAssistance a: myQuar) {
                myQuarantineList.add(a);
            }
        });
        t.start();
        t.join();

        ArrayAdapter<String> arr = new ArrayAdapter<>(getContext(), R.layout.spinner_item, names);
        sp.setAdapter(arr);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                homeType[0] = selectedItemText;

                if(position >= 0){
                    showShortToast("Selected : " + selectedItemText);
                }
            }
            public void onNothingSelected(AdapterView<?> parent){}
        });

        adapter = new CastomRequestsAdapter(getContext(), 0, myQuarantineList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);
            GeoPoint point = adapter.getItem(position).getLocation();

            @NonNull Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            @NonNull List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
                if(addresses.size() != 0) {
                    @NonNull String newCountry = addresses.get(0).getCountryName();
                    newIntent.putExtra("country", newCountry);

                    @NonNull String newCity = addresses.get(0).getLocality();
                    newIntent.putExtra("city", newCity);

                    @NonNull String newAddress = addresses.get(0).getThoroughfare();
                    newIntent.putExtra("address", newAddress);
                }
                else {//TODO: da togliere e verificare
                    newIntent.putExtra("country", "newCountry");
                    newIntent.putExtra("city", "newCity");
                    newIntent.putExtra("address", "newAddress");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            newIntent.putExtra("title",  adapter.getItem(position).getTitle());
            //newIntent.putExtra("date", adapter.getItem(position).getDateDeliveryToDate().toString()); //la prendo dall'altra parte
            newIntent.putExtra("id", adapter.getItem(position).getId());

            newIntent.putExtra("class", "Homenegative");

            startActivity(newIntent);
        }));

        //Poter prendere in carico una richiesta solo se sei negativo
        view.findViewById(R.id.takenRequest).setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);

            String id = null;
            if(inCharge[0] != null)
                id = inCharge[0].getId();
            Log.d("InCharge", id);

            newIntent.putExtra("country", "newCountry");
            newIntent.putExtra("city", "newCity");
            newIntent.putExtra("address", "newAddress");
            newIntent.putExtra("user", id);
            startActivity(newIntent);
        });


        view.findViewById(R.id.btn_chatNeg).setOnClickListener(v -> {
            //ci metto il collegamento alla chat
        });

    }

    public void refresh(View view) throws ExecutionException, InterruptedException { //probabile bisogno di refrescìh dei dati affinchè siano sempre aggiornati
        initViews(view);
    }
}