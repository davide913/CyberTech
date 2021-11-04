package it.unive.cybertech.assistenza;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.unive.cybertech.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList<String> localDataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private TextView textLocation;
        private TextView textDate;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textTitle = (TextView) view.findViewById(R.id.title_request);
            textLocation = (TextView) view.findViewById(R.id.location_request);
            textDate = (TextView) view.findViewById(R.id.date_request);

        }

        public TextView getTextTitle() {
            return textTitle;
        }
        public TextView getTextLocation() {
            return textLocation;
        }
        public TextView getTextDate() {
            return textDate;
        }
    }

    public ListAdapter(Context context , ArrayList<String> dataSet) {
        localDataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_request_home_visualisation, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textTitle.setText(localDataSet.get(position));
        holder.textLocation.setText(localDataSet.get(position));
        holder.textDate.setText(localDataSet.get(position));

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
