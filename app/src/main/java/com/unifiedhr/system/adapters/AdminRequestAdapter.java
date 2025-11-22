package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unifiedhr.system.R;
import com.unifiedhr.system.models.User;

import java.util.List;

public class AdminRequestAdapter extends RecyclerView.Adapter<AdminRequestAdapter.AdminRequestViewHolder> {

    private List<User> adminRequests;
    private OnAdminRequestListener listener;

    public interface OnAdminRequestListener {
        void onApprove(User user);
        void onReject(User user);
    }

    public AdminRequestAdapter(List<User> adminRequests, OnAdminRequestListener listener) {
        this.adminRequests = adminRequests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_request, parent, false);
        return new AdminRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRequestViewHolder holder, int position) {
        User user = adminRequests.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvCompanyName.setText(user.getCompanyId());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(user));
        holder.btnReject.setOnClickListener(v -> listener.onReject(user));
    }

    @Override
    public int getItemCount() {
        return adminRequests.size();
    }

    static class AdminRequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvCompanyName;
        Button btnApprove, btnReject;

        public AdminRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
