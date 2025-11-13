package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.unifiedhr.system.R;
import com.unifiedhr.system.models.DashboardItem;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private List<DashboardItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DashboardItem item);
    }

    public DashboardAdapter(List<DashboardItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            cardView = itemView.findViewById(R.id.cardViewItem);
        }

        public void bind(final DashboardItem item, final OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DashboardItem item = itemList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
