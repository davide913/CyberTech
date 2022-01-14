package it.unive.cybertech.noleggio;

import static it.unive.cybertech.utils.Utils.ItemClickListener;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.LendingInProgress;
import it.unive.cybertech.utils.Utils;

/**
 * This is an adapter that provide a view for the rented materials
 *
 * @author Mattia Musone
 */
public class RentedMaterialsAdapter extends RecyclerView.Adapter<RentedMaterialsAdapter.ViewHolder> {

    public static final String ID = "RentedMaterialsAdapter";
    private List<LendingInProgress> showcaseList;
    //A callback to call when an item is clicked
    private ItemClickListener clickListener;
    private String tag = ID;

    /**
     * A constructor that accepts and overrides the default tag/id of the class
     *
     * @param showcaseList the list of item to show
     * @param tag          The custom tag to add to each view
     */
    public RentedMaterialsAdapter(@NonNull List<LendingInProgress> showcaseList, String tag) {
        this.tag = tag;
        this.showcaseList = showcaseList;
    }

    /**
     * The constructor
     *
     * @param showcaseList the list of item to show
     */
    public RentedMaterialsAdapter(@NonNull List<LendingInProgress> showcaseList) {
        this.showcaseList = showcaseList;
    }

    /**
     * A viewholder that is used for caching the view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title, description;
        private final ImageView image;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.showcase_item_title);
            description = view.findViewById(R.id.showcase_item_description);
            image = view.findViewById(R.id.showcase_item_image);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

        /**
         * Bind a lending to it's field
         *
         * @param item     the lending that is about to be displayed
         * @param position The item position in the list
         */
        public void bind(@NonNull final LendingInProgress item, int position) {
            Utils.executeAsync(item::obtainMaterializedMaterial, new Utils.TaskResult<Material>() {
                @Override
                public void onComplete(Material result) {
                    title.setText(result.getTitle());
                    description.setText(result.getDescription());
                    itemView.setOnClickListener(v -> clickListener.onItemClick(v, position));
                    if (result.getPhoto() != null) {
                        byte[] arr = Base64.decode(result.getPhoto(), Base64.DEFAULT);
                        if (arr != null && arr.length > 0)
                            image.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }


    @NonNull
    @Override
    public RentedMaterialsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.showcase_list_item, parent, false);
        view.setTag(tag);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(showcaseList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return showcaseList.size();
    }

    /**
     * Sets the listener of the click item
     */
    void setClickListener(@NonNull ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * Upload the list item replacing with new ones
     *
     * @param materials the new list of lending
     * */
    public void setItems(@NonNull List<LendingInProgress> materials) {
        this.showcaseList = materials;
    }

    /**
     * Remove an item at the provided position and notify the adapter.
     * Note: no check is made about the index
     *
     * @param position The item position in the list
     * */
    public void removeAt(int position) {
        showcaseList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, 1);
    }

    /**
     * Add a new lending at the end of the list
     *
     * @param lending The item to add
     * */
    public void add(@NotNull LendingInProgress lending) {
        showcaseList.add(lending);
        notifyItemInserted(showcaseList.size() - 1);
        notifyItemRangeChanged(showcaseList.size() - 1, showcaseList.size());
    }
}
