package it.unive.cybertech.assistenza;

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

    public ListRequestsAdapter(List<String> list) {
        this.listRequests = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public TextView getTextView() {
            return textView;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //inserisci tutti i campi che hai creato di la sul xml
            textView = itemView.findViewById(R.id.textFull);
            textView = itemView.findViewById(R.id.textLocation);
            textView = itemView.findViewById(R.id.textDate);
            textView = itemView.findViewById(R.id.textTitle);

        }
    }

    @NonNull
    @Override
    public ListRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_request_visualisation, parent,  false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRequestsAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(listRequests.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
