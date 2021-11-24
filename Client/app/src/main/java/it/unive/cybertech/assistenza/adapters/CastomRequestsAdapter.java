package it.unive.cybertech.assistenza.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.QuarantineAssistance;

public class CastomRequestsAdapter extends ArrayAdapter {
    private List<String> myList;
    private List<QuarantineAssistance> myOtherList; //aggiunta questa lista
    private Context context;
    private static final String TAG = "Custom Request Adapter";
    private int index = 0;

    public CastomRequestsAdapter(@NonNull Context context, int resource, ArrayList<String> myList) { //TODO: non array di stringhe ma array di richieste prese su dal db
        super(context, resource, myList);
        this.myList = myList;
        this.context = context;
    }

    public CastomRequestsAdapter(@NonNull Context context, int resource, ArrayList<QuarantineAssistance> myList, int unused) { //TODO: non array di stringhe ma array di richieste prese su dal db
        super(context, resource, myList);
        this.myOtherList = myList;
        this.context = context;
    } //aggiunto questo secondo costruttore per i test

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "call getView");
        //QuarantineAssistance request = myOtherList.get(position); //aggiunto questo
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_request_home_visualisation, null);

        TextView title = view.findViewById(R.id.title_request);
        title.setText(myList.get(index)); ////TODO: request.getTitle();

        TextView location = view.findViewById(R.id.location_request);
        location.setText(myList.get(index)); //TODO: request.getLocation();

        TextView date = view.findViewById(R.id.date_request);
        date.setText(myOtherList.get(index).getDeliveryDate().toString());

        return view;
    }
}
