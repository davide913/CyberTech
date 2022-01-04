package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.getQuarantineAssistanceByInCharge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

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
        final ArrayAdapter<QuarantineAssistance>[] adapter = new ArrayAdapter[1];

        final ArrayList<QuarantineAssistance>[] myQuarantineList = new ArrayList[]{new ArrayList<>()};
        listView = view.findViewById(R.id.listRequests);

        final ArrayList<AssistanceType> adapterList = new ArrayList<>();
        final QuarantineAssistance[] inCharge = {null};
        final String[] homeType = new String[1];
        final List<QuarantineAssistance>[] myQuar = new List[]{null};
        final ArrayList<AssistanceType>[] tList = new ArrayList[]{null};
        final AssistanceType[] aux = {null};
        GeoPoint myGeoPosition = user.getLocation();
        Log.d("Paese", user.getCountry());
        Log.d("CIttà", user.getCity());


        Spinner sp = view.findViewById(R.id.homeNegSpinner);
        ArrayList<String> names = new ArrayList<>();

        Thread t = new Thread(() -> {
            try {
                tList[0] = AssistanceType.getAssistanceTypes();
                //TODO: da cambiare poi con un elemento dello spinner in posizione 0 generico che indica tutte le richieste
                myQuar[0] = QuarantineAssistance.getJoinableQuarantineAssistance(null, null, 10);

                inCharge[0] = getQuarantineAssistanceByInCharge(user);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            for (AssistanceType a: tList[0]) {
                names.add(a.getType());
                adapterList.add(a);
            }
            for (QuarantineAssistance a: myQuar[0]) {
                myQuarantineList[0].add(a);
            }
            Log.d("Dimensione primo Thread", String.valueOf(myQuar[0].size()));
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

                for (AssistanceType a: tList[0]) {
                    if(a.getType().equals(selectedItemText))
                        aux[0] = a;
                }

                Thread m = new Thread(() -> {
                    myQuarantineList[0] = new ArrayList<>();
                    Log.d("Dimensione dopo reset nel secondo Thread", String.valueOf(myQuarantineList[0].size()));
                    try {
                        myQuar[0] = QuarantineAssistance.getJoinableQuarantineAssistance(aux[0], myGeoPosition, 10);
                        Log.d("Tipo", aux[0].getType());
                        Log.d("Dimensione", String.valueOf(myQuar[0].size()));

                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (QuarantineAssistance a: myQuar[0]) {
                        myQuarantineList[0].add(a);
                    }

                    if(myQuarantineList[0] != null)
                        Log.d("Dimensione finale", String.valueOf(myQuarantineList[0].size()));
                });
                m.start();
                try {
                    m.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            public void onNothingSelected(AdapterView<?> parent){}
        });

        adapter[0] = new CastomRequestsAdapter(getContext(), 0, myQuarantineList[0]);
        listView.setAdapter(adapter[0]);

        listView.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);

            geoDecoder(adapter[0].getItem(position), newIntent);
            String taken = null;
            try {
                taken = inCharge[0].getId();
            }
            catch(NullPointerException ignored) {}

            newIntent.putExtra("alreadyTaken", taken);
            newIntent.putExtra("id", adapter[0].getItem(position).getId());
            newIntent.putExtra("class", "Homenegative");

            startActivityForResult(newIntent, 1);
        }));

        //Poter prendere in carico una richiesta solo se sei negativo
        view.findViewById(R.id.alreadyTaken).setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);

            String id = null;
            if(inCharge[0] != null) {
                id = inCharge[0].getId();
                geoDecoder(inCharge[0], newIntent);

                newIntent.putExtra("user", id);
            }
            startActivityForResult(newIntent, 0);
        });
    }

    private void updateFr(){  //Permette di aggiornare i fragments
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.assistenza.HomePageNegative()).commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        updateFr();
    }

    private void geoDecoder(QuarantineAssistance request, Intent newIntent){
        GeoPoint point = request.getLocation();

        @NonNull Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        @NonNull List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            Log.d("Latitudine", String.valueOf(point.getLatitude()));
            Log.d("Longitudine", String.valueOf(point.getLongitude()));
            if(addresses.size() != 0) {
                newIntent.putExtra("country",addresses.get(0).getCountryName());
                newIntent.putExtra("city", addresses.get(0).getLocality());
            }
            else {      //per richieste in posizioni estreme
                newIntent.putExtra("country", "Out of Bounds");
                newIntent.putExtra("city", "Out of Bounds");
            }
            newIntent.putExtra("title",  request.getTitle());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}