package it.unive.cybertech.groups.activities;

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

import it.unive.cybertech.database.Groups.Activity;
import it.unive.cybertech.utils.Utils.ItemClickListener;

/**
 * Adapter used by class "{@link it.unive.cybertech.groups.activities.GroupActivities}" to manage the groups list
 *
 * @author Daniele Dotto
 * @since 1.1
 */
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ViewHolder> {

    private @Nullable
    List<Activity> activities;
    private @Nullable
    ItemClickListener clickListener;

    public ActivityListAdapter(@NonNull final List<Activity> activities) {
        this.activities = activities;
    }

    /**
     * Return the entire group activities list only if that is not null.
     *
     * @return "{@link #activities}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    List<Activity> getActivities() {
        return Objects.requireNonNull(activities);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @NonNull LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @NonNull View view = inflater.inflate(R.layout.activity_list_item, parent, false);
        return new ActivityListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getActivities().get(position), position);
    }

    /**
     * It needs to know how many group activities are present in selected group.
     *
     * @return the "{@link #activities}" size.
     * @author Daniele Dotto
     * @since 1.1
     */
    @Override
    public int getItemCount() {
        return getActivities().size();
    }

    void setClickListener(@NonNull ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * Allow to update the entire group activities list.
     *
     * @param activities The new group activities list passed by user that substitute the old one
     * @author Daniele Dotto
     * @since 1.1
     */
    public void setItems(List<Activity> activities) {
        this.activities = activities;
    }


    /**
     * Allow to manage the individual group activity item located in RecyclerView
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title, description, date, location, participants;

        /**
         * Bind the text info to layout TextViews
         *
         * @param view The fragment/activity view
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.activity_item_title);
            description = view.findViewById(R.id.activity_item_description);
            date = view.findViewById(R.id.activity_item_date);
            location = view.findViewById(R.id.activity_item_location);
            participants = view.findViewById(R.id.activity_item_participants);
        }


        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onItemClick(view, getAdapterPosition());
        }

        /**
         * Set the text info to layout TextViews.
         *
         * @param item     The clicked group activity by the user
         * @param position Item list position
         * @author Daniele Dotto
         * @since 1.1
         */
        public void bind(final @NonNull Activity item, int position) {
            title.setText(item.getName());
            description.setText(item.getDescription());
            date.setText(""); // todo pattern
            location.setText(item.getPlace());
            participants.setText(String.valueOf(item.getParticipants().size()));
            itemView.setOnClickListener(v -> Objects.requireNonNull(clickListener).onItemClick(v, position));
        }
    }
}
