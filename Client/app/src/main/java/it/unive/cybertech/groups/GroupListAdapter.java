package it.unive.cybertech.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;

import static it.unive.cybertech.utils.Utils.ItemClickListener;

/**
 * Adapter used by class "{@link it.unive.cybertech.groups.HomePage}" to manage the groups list
 * @author Daniele Dotto
 * @since 1.1
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private @Nullable List<Group> groups;
    private @Nullable ItemClickListener clickListener;

    public GroupListAdapter(@NonNull List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Return the entire group list only if that is not null.
     * @return "{@link #groups}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull List<Group> getGroups() {
        return Objects.requireNonNull(groups);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        @NonNull LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @NonNull View view = inflater.inflate(R.layout.group_list_item, parent,false);
        return new GroupListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getGroups().get(position), position);
    }

    /**
     * It needs to know how many groups are present in Families Share (plugin) community.
     * @return the "{@link #groups}" size.
     * @author Daniele Dotto
     * @since 1.1
     */
    @Override
    public int getItemCount() {
        return getGroups().size();
    }

    void setClickListener(@NonNull ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * Allow to update the entire group list.
     * @param groups The new group list passed by user that substitute the old one
     * @author Daniele Dotto
     * @since 1.1
     */
    public void setItems(@NonNull final List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Allow to manage the individual group item located in RecyclerView
     * @author Daniele Dotto
     * @since 1.1
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final @NonNull TextView title, description, participants;

        /**
         * Bind the text info to layout TextViews
         * @param view The fragment/activity view
         */
        public ViewHolder(@NonNull final View view) {
            super(view);
            title = view.findViewById(R.id.group_item_title);
            description = view.findViewById(R.id.group_item_description);
            participants = view.findViewById(R.id.group_item_participants);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

        /**
         * Set the text info to layout TextViews.
         * @param item The clicked group by the user
         * @param position Item list position
         * @author Daniele Dotto
         * @since 1.1
         */
        public void bind(@NonNull final Group item, int position) {
            title.setText(item.getName());
            description.setText(item.getDescription());
            participants.setText(String.valueOf(item.getMembers().size()));
            itemView.setOnClickListener(v -> Objects.requireNonNull(clickListener).onItemClick(v, position));
        }
    }
}
