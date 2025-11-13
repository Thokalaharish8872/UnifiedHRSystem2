package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.User;

import java.util.List;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerViewHolder> {
    private List<User> managerList;

    public ManagerAdapter(List<User> managerList) {
        this.managerList = managerList;
    }

    @NonNull
    @Override
    public ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new ManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerViewHolder holder, int position) {
        User user = managerList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvEmployeeId.setText("Manager ID: " + user.getUserId());
    }

    @Override
    public int getItemCount() {
        return managerList.size();
    }

    static class ManagerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvEmployeeId;

        ManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
        }
    }
}








