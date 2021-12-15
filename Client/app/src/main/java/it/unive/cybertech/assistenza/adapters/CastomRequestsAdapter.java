package it.unive.cybertech.assistenza.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.AssistanceType;
import it.unive.cybertech.database.Profile.QuarantineAssistance;

public class CastomRequestsAdapter extends ArrayAdapter {
    private List<QuarantineAssistance> myList;
    private Context context;
    private static final String TAG = "Custom Request Adapter";
    private int index = 0;
    ArrayList<AssistanceType> type;
    private ArrayList<String> prova;

    public CastomRequestsAdapter(@NonNull Context context, int resource, ArrayList<QuarantineAssistance> myList, ArrayList<AssistanceType> type) {
        super(context, resource, myList);
        this.myList = myList;
        this.context = context;
        this.type = type;
    }

    //TODO: se la chiamo dalla Request.Viz non ho bisogno del tipo, chiamo tutte le mie
    public CastomRequestsAdapter(@NonNull Context context, int resource, ArrayList<QuarantineAssistance> myList) {
        super(context, resource, myList);
        this.myList = myList;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "call getView");
        QuarantineAssistance request = myList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_request_home_visualisation, null);

        TextView title = view.findViewById(R.id.title_request);
        title.setText(request.getTitle());

        GeoPoint point = request.getLocation();
        showAddress(point, view);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void showAddress(@NonNull GeoPoint point, View view) {
        @NonNull Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        @NonNull List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
            if(addresses.size() != 0) {
                @NonNull String newCountry = addresses.get(0).getCountryName();
                TextView country = view.findViewById(R.id.country_request);
                country.setText(newCountry);

                @NonNull String newCity = addresses.get(0).getLocality();
                TextView city = view.findViewById(R.id.city_location);
                city.setText(newCity);

                @NonNull String newAddress = addresses.get(0).getThoroughfare();
                /*TextView address = view.findViewById(R.id.address_location);
                address.setText(newAddress);*/
            }
            else{//TODO: da togliere e verificare
                TextView country = view.findViewById(R.id.country_request);
                country.setText("newCountry");

                TextView city = view.findViewById(R.id.city_location);
                city.setText("newCity");

                /*TextView address = view.findViewById(R.id.address_location);
                address.setText("newAddress");*/
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
