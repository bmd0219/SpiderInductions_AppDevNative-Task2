package com.example.nasa_observations_bmd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private List<SearchResult.Collection.Item> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchResult.Collection.Item item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecyclerViewAdapter(List<SearchResult.Collection.Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card_view, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        SearchResult.Collection.Item item = items.get(position);

        holder.nasaIdTextView.setText("NASA_ID: " + item.getData().get(0).getNasa_id());
        holder.titleTextView.setText("TITLE" + item.getData().get(0).getTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView nasaIdTextView;
        private TextView titleTextView;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            nasaIdTextView = itemView.findViewById(R.id.nasa_id_text_view);
            titleTextView = itemView.findViewById(R.id.title_text_view);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(items.get(position));
                }
            });
        }
    }
}
