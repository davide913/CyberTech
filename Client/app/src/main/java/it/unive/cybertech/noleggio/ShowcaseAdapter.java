package it.unive.cybertech.noleggio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unive.cybertech.R;
import it.unive.cybertech.assistenza.RequestInfo;
import it.unive.cybertech.assistenza.RequestViz;

public class ShowcaseAdapter extends RecyclerView.Adapter<ShowcaseAdapter.ViewHolder>{

    private final List<String> showcaseList;
    private ItemClickListener clickListener;

    public ShowcaseAdapter(List<String> showcaseList) {
        this.showcaseList = showcaseList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title, description;
        private final ImageView image;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.shwcase_item_title);
            description = view.findViewById(R.id.showcase_item_description);
            image = view.findViewById(R.id.showcase_item_image);
        }

        /*public TextView getTitle() {
            return title;
        }
        public TextView getDescription() {
            return description;
        }
        public ImageView getImage() {
            return image;
        }*/

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

        public void bind(final String item, int position) {
            title.setText(item);
            description.setText(item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    clickListener.onItemClick(v, position);
                }
            });
        }
    }


    @NonNull
    @Override
    public ShowcaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.showcase_list_item, null);
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

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
