package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.KRA;

import java.util.List;

public class KRAAdapter extends RecyclerView.Adapter<KRAAdapter.KRAViewHolder> {
    private List<KRA> kraList;

    public KRAAdapter(List<KRA> kraList) {
        this.kraList = kraList;
    }

    @NonNull
    @Override
    public KRAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kra, parent, false);
        return new KRAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KRAViewHolder holder, int position) {
        KRA kra = kraList.get(position);
        holder.tvTitle.setText(kra.getTitle());
        holder.tvDescription.setText(kra.getDescription());
        holder.tvProgress.setText("Progress: " + kra.getCurrentProgress() + " / " + kra.getTarget());
    }

    @Override
    public int getItemCount() {
        return kraList.size();
    }

    static class KRAViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvProgress;

        KRAViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvProgress = itemView.findViewById(R.id.tvProgress);
        }
    }
}








