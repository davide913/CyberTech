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
        QuarantineAssistance request = myList.get(position); //aggiunto questo
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_request_home_visualisation, null);

        //TODO: splittare in base al chiamante, myList se la chiama la negative deve avere le richieste da poter accettare, altrimenti quelle fatte da lui
        try {
            myList = QuarantineAssistance.getJoinableQuarantineAssistance(null, null, 10);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        TextView title = view.findViewById(R.id.title_request);
        title.setText(request.getTitle());

        GeoPoint point = request.getLocation();
        showAddress(point, view);
        //TODO: manca il reference alla list_proto?
        /*
        TextView country = view.findViewById(R.id.country_request);
        country.setText(myList.get(index).getLocation().toString()); //TODO: convertire una Stringa in GeoPoint

        TextView city = view.findViewById(R.id.city_location);
        city.setText(myList.get(index).getLocation().toString());

        TextView address = view.findViewById(R.id.address_location);
        address.setText(myList.get(index).getLocation().toString());
         */

        TextView date = view.findViewById(R.id.date_request);
        date.setText(request.getDeliveryDate().toString());

        return view;
    }

    private void showAddress(@NonNull GeoPoint point, View view) {
        @NonNull Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        @NonNull List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);

            @NonNull String newCountry = addresses.get(0).getCountryName();
            TextView country = view.findViewById(R.id.country_request);
            country.setText(newCountry);

            @NonNull String newCity = addresses.get(0).getLocality();
            TextView city = view.findViewById(R.id.city_location);
            city.setText(newCity);

            @NonNull String newAddress = addresses.get(0).getThoroughfare();
            TextView address = view.findViewById(R.id.address_location);
            address.setText(newAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
