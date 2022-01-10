package it.unive.cybertech.noleggio;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Utils.ItemClickListener;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;

public class ShowcaseAdapter extends RecyclerView.Adapter<ShowcaseAdapter.ViewHolder> {

    private List<Material> showcaseList;
    private ItemClickListener clickListener;

    public ShowcaseAdapter(List<Material> showcaseList) {
        this.showcaseList = showcaseList;
    }

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

        public void bind(final Material item, int position) {
            String titleStr = item.getTitle();
            if (item.getOwner().getId().equals(user.getId())) {
                titleStr += " (TUO)";
                title.setTextColor(Color.BLUE);
            }
            title.setText(titleStr);
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
    public ShowcaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.showcase_list_item, parent, false);
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

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void setItems(List<Material> materials) {
        this.showcaseList = materials;
    }

    public Material removeAt(int position) {
        Material removed = showcaseList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, showcaseList.size());
        return removed;
    }

    public void add(Material m) {
        showcaseList.add(m);
        notifyItemInserted(showcaseList.size() - 1);
    }
}
