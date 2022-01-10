package it.unive.cybertech.assistenza.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.QuarantineAssistance;
import it.unive.cybertech.utils.Utils;

/**
 * A Custom Adapter the visualisation of requests with a balloon effect
 * @author Mihail Racaru
 * @since 1.1
 */
public class CustomRequestsAdapter extends ArrayAdapter<QuarantineAssistance> {
    private final List<QuarantineAssistance> myList;
    private final Context context;
    private static final String TAG = "Custom Request Adapter";

    public CustomRequestsAdapter(@NonNull Context context, int resource, List<QuarantineAssistance> myList) {
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

        TextView dateView =  view.findViewById(R.id.date_request);
        String strDate = Utils.formatDateToString(request.getDeliveryDateToDate(), "kk:mm  dd/MM" );
        dateView.setText(strDate);

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
            }
            else{
                TextView country = view.findViewById(R.id.country_request);
                country.setText("Out of Bounds");

                TextView city = view.findViewById(R.id.city_location);
                city.setText("Out of Bounds");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
