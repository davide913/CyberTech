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

import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;

/**
 * This is an adapter that provide a view for the rent materials (my materials in the showcase for other users)
 *
 * @author Mattia Musone
 */
public class RentMaterialAdapter extends RecyclerView.Adapter<RentMaterialAdapter.ViewHolder> {

    public static final String ID = "RentMaterialAdapter";
    private List<Material> showcaseList;
    //A callback to call when an item is clicked
    private ItemClickListener clickListener;

    /**
     * The constructor
     *
     * @param showcaseList the list of item to show
     */
    public RentMaterialAdapter(@NonNull List<Material> showcaseList) {
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
        public void bind(@NonNull final Material item, int position) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            itemView.setOnClickListener(v -> clickListener.onItemClick(v, position));
            if (item.getPhoto() != null) {
                byte[] arr = Base64.decode(item.getPhoto(), Base64.DEFAULT);
                if (arr != null && arr.length > 0)
                    image.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
            }
        }
    }


    @NonNull
    @Override
    public RentMaterialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.showcase_list_item, parent, false);
        view.setTag(ID);
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
     */
    public void setItems(@NonNull List<Material> materials) {
        this.showcaseList = materials;
    }

    /**
     * Remove an item at the provided position and notify the adapter.
     *
     * @param position The item position in the list
     */
    public void removeAt(int position) {
        if (position > 0) {
            showcaseList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, showcaseList.size());
        }
    }

    /**
     * Add a new lending at the end of the list
     *
     * @param material The item to add
     */
    public void add(@NonNull Material material) {
        showcaseList.add(material);
        notifyItemInserted(showcaseList.size() - 1);
    }
}
