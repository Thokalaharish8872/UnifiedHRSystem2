package com.unifiedhr.system.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unifiedhr.system.R;
import com.unifiedhr.system.models.KRA;
import com.unifiedhr.system.ui.UpdateKRAProgressActivity;

import java.util.List;

public class KRAAdapter extends RecyclerView.Adapter<KRAAdapter.KRAViewHolder> {

    public interface KRAActionListener {
        void onEdit(KRA kra);
        void onDelete(KRA kra);
    }

    private final List<KRA> kraList;
    private final boolean showUpdateButton;
    private final boolean allowEditDelete;
    private final KRAActionListener listener;

    public KRAAdapter(List<KRA> kraList,
                      boolean showUpdateButton,
                      boolean allowEditDelete,
                      KRAActionListener listener) {

        this.kraList = kraList;
        this.showUpdateButton = showUpdateButton;
        this.allowEditDelete = allowEditDelete;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KRAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kra, parent, false);
        return new KRAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KRAViewHolder holder, int position) {
        KRA kra = kraList.get(position);

        holder.tvTitle.setText(kra.getTitle());
        holder.tvDescription.setText(kra.getDescription());
        holder.tvProgress.setText("Progress: " + kra.getCurrentProgress() + " / " + kra.getTarget());
        holder.tvDeadline.setText("Deadline: " + kra.getDeadline());

        holder.btnUpdate.setVisibility(showUpdateButton ? View.VISIBLE : View.GONE);

        holder.btnEdit.setVisibility(allowEditDelete ? View.VISIBLE : View.GONE);
        holder.btnDelete.setVisibility(allowEditDelete ? View.VISIBLE : View.GONE);

        holder.btnUpdate.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), UpdateKRAProgressActivity.class);
            i.putExtra("kraId", kra.getKraId());
            i.putExtra("companyId", kra.getCompanyId());
            v.getContext().startActivity(i);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(kra);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(kra);
        });
    }

    @Override
    public int getItemCount() {
        return kraList.size();
    }

    static class KRAViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvProgress, tvDeadline;
        Button btnUpdate, btnEdit, btnDelete;

        public KRAViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            btnUpdate = itemView.findViewById(R.id.btnUpdateProgress);
            btnEdit = itemView.findViewById(R.id.btnEditKRA);
            btnDelete = itemView.findViewById(R.id.btnDeleteKRA);
        }
    }
}
