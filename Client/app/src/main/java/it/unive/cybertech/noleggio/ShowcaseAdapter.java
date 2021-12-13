package it.unive.cybertech.noleggio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import static it.unive.cybertech.utils.Utils.ItemClickListener;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;

public class ShowcaseAdapter extends RecyclerView.Adapter<ShowcaseAdapter.ViewHolder>{

    private final List<Material> showcaseList;
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
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            itemView.setOnClickListener(v -> clickListener.onItemClick(v, position));
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
}
