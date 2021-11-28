package it.unive.cybertech.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unive.cybertech.R;
import static it.unive.cybertech.utils.Utils.ItemClickListener;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    //private List<Groups> groups;
    private List<String> groups;
    private ItemClickListener clickListener;

    public GroupListAdapter(List<String> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.group_list_item, parent,false);
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

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title, description, participants, location;
        //private final ImageView image;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.group_item_title);
            description = view.findViewById(R.id.group_item_description);
            participants = view.findViewById(R.id.group_item_participants);
            location = view.findViewById(R.id.group_item_location);
            //image = view.findViewById(R.id.showcase_item_image);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

        public void bind(final String item, int position) {
            title.setText(item);//.getName());
            description.setText(item);//.getDescription());
            participants.setText(item);//String.valueOf(item.getMembers().size()));
            location.setText("item.");
            itemView.setOnClickListener(v -> clickListener.onItemClick(v, position));
        }
    }
}
