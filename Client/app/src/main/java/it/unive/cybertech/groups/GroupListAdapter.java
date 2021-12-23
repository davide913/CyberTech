package it.unive.cybertech.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;

import static it.unive.cybertech.utils.Utils.ItemClickListener;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<Group> groups;
    private ItemClickListener clickListener;

    public GroupListAdapter(@NonNull List<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @NonNull LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @NonNull View view = inflater.inflate(R.layout.group_list_item, parent,false);
        return new GroupListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(groups.get(position), position);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    void setClickListener(@NonNull ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void setItems(List<Group> groups) {
        this.groups = groups;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final @NonNull TextView title, description, participants, location;
        //private final ImageView image; todo immagine gruppo

        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.group_item_title);
            description = view.findViewById(R.id.group_item_description);
            participants = view.findViewById(R.id.group_item_participants);
            location = view.findViewById(R.id.group_item_location);
            //image = view.findViewById(R.id.showcase_item_image); todo immagine gruppo
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

        public void bind(final @NonNull Group item, int position) {
            title.setText(item.getName());                  // todo da togliere "" quando esisterà un gruppo creato da un utente
            description.setText(item.getDescription());     // todo da togliere "" quando esisterà un gruppo creato da un utente
            participants.setText(String.valueOf(item.getMembers().size())); // todo da togliere "" quando esisterà un gruppo creato da un utente
            location.setText("item.getLocation()");           // todo da togliere "" quando esisterà un gruppo creato da un utente
            itemView.setOnClickListener(v -> clickListener.onItemClick(v, position));
        }
    }
}
