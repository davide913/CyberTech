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

import java.util.List;

import it.unive.cybertech.R;

public class ListRequestsAdapter extends RecyclerView.Adapter<ListRequestsAdapter.ViewHolder> {
    private List<String> listRequests;
    private Context context;
    public String ciao = "ciao";

    public ListRequestsAdapter(Context context,List<String> list) {
        this.context = context;
        this.listRequests = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textFull;
        private TextView textLocation;
        private TextView textDate;
        private TextView textTitle;
        public String ciao = "ciao";


        public TextView getTextTitle() {
            return textTitle;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //inserisci tutti i campi che hai creato di la sul xml
            textFull = itemView.findViewById(R.id.textFull);
            textLocation = itemView.findViewById(R.id.textLocation);
            textDate = itemView.findViewById(R.id.textDate);
            textTitle = itemView.findViewById(R.id.title_request);
            textTitle.setText(ciao);
        }
    }

    @NonNull
    @Override
    public ListRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.activity_request_home_visualisation, null);

        TextView txt = view.findViewById(R.id.title_request);
        txt.setText("ciao");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRequestsAdapter.ViewHolder holder, int position) {
        holder.getTextTitle().setText(listRequests.get(position).toString());
        TextView textTitle = holder.textTitle;

        textTitle.setText(ciao);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
