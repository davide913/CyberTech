package it.unive.cybertech.gestione_covid.adapters;

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
import it.unive.cybertech.database.Groups.Group;

public class CustomSignReceivedAdapter extends ArrayAdapter {
    private static final String TAG = "CustomOrderArrayAdapterTrader";
    private Context context;
    private List<Group> groups;

    private Group group;


    public CustomSignReceivedAdapter(@NonNull Context context, int resource, List<Group> groups)/*TODO qua prenderà in ingresso un array di USER userList*/ {
        super(context, resource, groups);
        this.context = context;
        this.groups = groups;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent){
        Log.d(TAG, "call getView");
        group = groups.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.raw_list_sign_reported, null);



        TextView nameGroup = view.findViewById(R.id.textView_changeDate);
        TextView StatusTamp = view.findViewById(R.id.textView_changeStatus);


        nameGroup.setText(group.getName());
        StatusTamp.setText("Positivo");

        return view;
    }
}
