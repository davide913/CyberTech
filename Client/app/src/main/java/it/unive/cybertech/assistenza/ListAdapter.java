package it.unive.cybertech.assistenza;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    List<RequestInfo> requestInfoList;
    private Context context;

    public ListAdapter(List<RequestInfo> requestInfoList, Context context) {
        this.requestInfoList = new ArrayList<RequestInfo>();
        this.requestInfoList = requestInfoList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textLocation;
        TextView textDate;
        RelativeLayout parentLayout;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textTitle = (TextView) view.findViewById(R.id.title_request);
            textLocation = (TextView) view.findViewById(R.id.location_request);
            textDate = (TextView) view.findViewById(R.id.date_request);
            parentLayout = view.findViewById(R.id.list_proto);
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


    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_request_home_visualisation, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textTitle.setText(requestInfoList.get(position).getTitle());
        holder.textLocation.setText(requestInfoList.get(position).getLocation());
        holder.textDate.setText(requestInfoList.get(position).getDate());
        holder.parentLayout.setOnClickListener(view ->{
            //mando avanti i dati
            //Ho modificato da qui -----> 08/11/2021
            Intent intent = new Intent(context, RequestViz.class);
            //intent.putExtra("lista", requestInfoList);
            intent.putExtra("title", requestInfoList.get(position).getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if(requestInfoList.isEmpty())
            return 0;
        return requestInfoList.size();
    }
}
