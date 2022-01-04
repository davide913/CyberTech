package it.unive.cybertech.assistenza;

import static it.unive.cybertech.database.Profile.QuarantineAssistance.getQuarantineAssistanceByInCharge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.adapters.CastomRequestsAdapter;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.Exception.NoQuarantineAssistanceFoundException;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.CachedUser;
import it.unive.cybertech.utils.Utils;

public class HomePagePositive extends Fragment {
    ListView listAlreadyMade;
    User user = CachedUser.user;

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
        ArrayAdapter<QuarantineAssistance> adapter;
        final List<QuarantineAssistance>[] myRequestsList = new List[]{new ArrayList<>()};
        final QuarantineAssistance[] myreq = new QuarantineAssistance[1];
        listAlreadyMade = view.findViewById(R.id.lst_myRequests);

        Thread t = new Thread(() -> {
            try {
                myRequestsList[0] = user.getMaterializedQuarantineAssistance();
            }
            catch (InterruptedException | ExecutionException |NoQuarantineAssistanceFoundException ignored) {}
        });
        t.start();
        t.join();

        if(myRequestsList[0] == null) {

            Utils.Dialog dialog = new Utils.Dialog(getContext());
            dialog.show("Informazione", "Se vuoi chiedere aiuto ad un volontario, clicca il tast 'Richiedi Assistenza' per ricevere aiuto");
            dialog.setCallback(new Utils.DialogResult() {
                                   @Override
                                   public void onSuccess() {
                                   }

                                   @Override
                                   public void onCancel() {
                                   }
                               });
        }

        adapter = new CastomRequestsAdapter(getContext(), 0, myRequestsList[0]);
        listAlreadyMade.setAdapter(adapter);
        listAlreadyMade.setOnItemClickListener(((parent, view1, position, id) -> {
            Intent newIntent = new Intent(getContext(), RequestViz.class);
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm  dd-MM");
            Date date = myRequestsList[0].get(position).getDeliveryDateToDate();
            String strDate = dateFormat.format(date);


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
                }
                else {
                    newIntent.putExtra("country", "Out of Bounds");
                    newIntent.putExtra("city", "Out of Bounds");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            newIntent.putExtra("title", adapter.getItem(position).getTitle());
            //newIntent.putExtra("date", strDate);
            newIntent.putExtra("id", adapter.getItem(position).getId());

            newIntent.putExtra("class", "positive"); //per indicare se il chiamante è la HomePositive o Negative
            startActivityForResult(newIntent, 4);
        }));


        view.findViewById(R.id.add_new_request).setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(), RequestDetails.class);

            startActivityForResult(newIntent, 2);
        });
    }

    private void updateFr(){  //Permette di aggiornare i fragments
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_fragment_content, new it.unive.cybertech.assistenza.HomePagePositive()).commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        updateFr();
    }

    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
