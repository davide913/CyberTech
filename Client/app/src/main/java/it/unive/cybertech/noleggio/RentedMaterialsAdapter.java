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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Material.Material;
import it.unive.cybertech.database.Profile.LendingInProgress;

public class RentedMaterialsAdapter extends RecyclerView.Adapter<RentedMaterialsAdapter.ViewHolder> {

    public static final String ID = "RentedMaterialsAdapter";
    private List<LendingInProgress> showcaseList;
    private ItemClickListener clickListener;
    private String tag = ID;

    public RentedMaterialsAdapter(List<LendingInProgress> showcaseList, String tag) {
        this.tag = tag;
        this.showcaseList = showcaseList;
    }

    public RentedMaterialsAdapter(List<LendingInProgress> showcaseList) {
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

        public void bind(final LendingInProgress item, int position) {
            AtomicReference<Material> material = new AtomicReference<>();
            Thread t = new Thread(() -> {
                try {
                    material.set(item.getMaterializedMaterial());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            title.setText(material.get().getTitle());
            description.setText(material.get().getDescription());
            itemView.setOnClickListener(v -> clickListener.onItemClick(v, position));
            if (material.get().getPhoto() != null) {
                byte[] arr = Base64.decode(material.get().getPhoto(), Base64.DEFAULT);
                if (arr != null && arr.length > 0)
                    image.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
            }
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

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void setItems(List<LendingInProgress> materials) {
        this.showcaseList = materials;
    }

    public void removeAt(int position) {
        showcaseList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, showcaseList.size());
    }

    public void add(@NotNull LendingInProgress m) {
        showcaseList.add(m);
        notifyItemInserted(showcaseList.size() - 1);
        //notifyItemRangeChanged(showcaseList.size() - 1, showcaseList.size());
    }
}
