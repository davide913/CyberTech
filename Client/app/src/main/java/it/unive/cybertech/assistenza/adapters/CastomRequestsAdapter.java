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

public class CastomRequestsAdapter extends ArrayAdapter {
    private List<String> myList;
    private Context context;
    private static final String TAG = "Custom Request Adapter";
    private int index = 0;

    public CastomRequestsAdapter(@NonNull Context context, int resource, ArrayList<String> myList) {
        super(context, resource, myList);
        this.myList = myList;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "call getView");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_request_home_visualisation, null);

        TextView title = view.findViewById(R.id.title_request);

        title.setText(myList.get(index));

        return view;
    }
}
