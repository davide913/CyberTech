package it.unive.cybertech.assistenza.adapters;

import static it.unive.cybertech.utils.CachedUser.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Profile.Chat;
import it.unive.cybertech.utils.Utils;

/**
 * This is an adapter that provide a view for the chat.
 * We have two layout, one from the point of view of the sender (MESSAGE_SENT) and the other one from the receiver (MESSAGE_RECEIVED)
 * If the current user equals the user that has sent the message show the first layout otherwise the other one
 *
 * @author Mattia Musone
 * */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int MESSAGE_SENT = 0;
    private static final int MESSAGE_RECEIVED = 1;

    private List<Chat.Message> showcaseList;
    private Utils.ItemClickListener clickListener;

    /**
     * The constructor
     *
     * @param chats the list of item to show
     */
    public ChatAdapter(@NonNull List<Chat.Message> chats) {
        this.showcaseList = chats;
    }

    /**
     * A viewholder that is used for caching the view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView message, time;

        public ViewHolder(View view, int type) {
            super(view);
            //Bind the view for the sender
            if (type == MESSAGE_SENT) {
                message = view.findViewById(R.id.quarantine_assistance_chat_sender_text);
                time = view.findViewById(R.id.quarantine_assistance_chat_sender_time);
                //Bind the view for the viewer
            } else {
                message = view.findViewById(R.id.assistance_other_chat_text);
                time = view.findViewById(R.id.assistance_other_chat_time);
            }
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

        /**
         * Bind the message to the view setting the text and the time
         *
         * @param item     the lending that is about to be displayed
         * @param position The item position in the list
         */
        public void bind(@NonNull final Chat.Message item, int position) {
            message.setText(item.getMessage());
            time.setText(Utils.formatDateToString(item.obtainDateTimeToDate(), "HH:mm"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chat.Message message = showcaseList.get(position);
        if (message.amITheSender(user))
            return MESSAGE_SENT;
        else
            return MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (viewType == MESSAGE_SENT) {
            view = inflater.inflate(R.layout.assistance_chat_sender_view, parent, false);
        } else if (viewType == MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.assistance_chat_receiver_view, parent, false);
        }
        return new ChatAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.bind(showcaseList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return showcaseList.size();
    }

    /**
     * Sets the listener of the click item
     */
    public void setClickListener(@NonNull Utils.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * Upload the list item replacing with new ones
     *
     * @param chats the new list of lending
     */
    public void setItems(@NonNull List<Chat.Message> chats) {
        this.showcaseList = chats;
    }

    /**
     * Remove an item at the provided position and notify the adapter.
     * Note: no check is made about the index
     *
     * @param position The item position in the list
     */
    public Chat.Message removeAt(int position) {
        Chat.Message removed = showcaseList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, showcaseList.size());
        return removed;
    }

    /**
     * Add a new chat message at the end of the list
     *
     * @param chat The item to add
     */
    public void add(@NonNull Chat.Message chat) {
        showcaseList.add(chat);
        notifyItemInserted(showcaseList.size() - 1);
    }
}