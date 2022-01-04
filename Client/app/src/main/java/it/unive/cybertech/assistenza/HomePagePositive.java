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
/***
 * QUesto commento per le classi
 */
public class HomePagePositive extends Fragment {
    ListView listAlreadyMade;
    User user = CachedUser.user;
    private ArrayAdapter<QuarantineAssistance> adapter;
    private List<QuarantineAssistance> myRequestsList = new ArrayList<>();

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
        listAlreadyMade = view.findViewById(R.id.lst_myRequests);

        Utils.executeAsync(() -> user.getMaterializedQuarantineAssistance(), new Utils.TaskResult<List<QuarantineAssistance>>() {
            @Override
            public void onComplete(List<QuarantineAssistance> result) {
                myRequestsList = result;
                message_if_empty();

                adapter = new CastomRequestsAdapter(getContext(), 0, myRequestsList);
                listAlreadyMade.setAdapter(adapter);

                listAlreadyMade.setOnItemClickListener(((parent, view1, position, id) -> {
                    Intent newIntent = new Intent(getContext(), RequestViz.class);

                    String strDate = Utils.formatDateToString(myRequestsList.get(position).getDeliveryDateToDate(), "kk:mm  dd-MM" ); //TODO: esempio di data con utils
                    geoPointer(newIntent, position);
                    putExtra(newIntent, position, strDate);

                    startActivityForResult(newIntent, 4);

                    //adapter.notifyDataSetChanged(); TODO: se si fa Async
                }));
            }

            @Override
            public void onError(Exception e) {

            }
        });

        view.findViewById(R.id.add_new_request).setOnClickListener(v -> {
            Intent newIntent = new Intent(getContext(), RequestDetails.class);

            startActivityForResult(newIntent, 2);
        });
    }

    private void putExtra(Intent newIntent, int position, String strDate) {
        newIntent.putExtra("title", adapter.getItem(position).getTitle());
        newIntent.putExtra("date", strDate);
        newIntent.putExtra("id", adapter.getItem(position).getId());
        newIntent.putExtra("class", "positive"); //per indicare se il chiamante è la HomePositive o Negative
    }

    private void geoPointer(Intent newIntent, int position) {
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
    }

    private void message_if_empty() {
        if(myRequestsList.size() == 0) {
            Utils.Dialog dialog = new Utils.Dialog(getContext());
            dialog.show(getString(R.string.information), getString(R.string.request_help_assistance)); //TODO: questo per i messaggi di info
            dialog.setCallback(new Utils.DialogResult() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onCancel() {
                }
            });
        }
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
}
