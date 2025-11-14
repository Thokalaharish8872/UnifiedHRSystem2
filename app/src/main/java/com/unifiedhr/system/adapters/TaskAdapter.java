package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private String userRole;
    private TaskActionListener listener;

    public interface TaskActionListener {
        void onUpdateStatus(Task task);
    }

    public TaskAdapter(List<Task> taskList, String userRole, TaskActionListener listener) {
        this.taskList = taskList;
        this.userRole = userRole;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvDeadline.setText(holder.itemView.getContext()
                .getString(R.string.label_deadline_value, task.getDeadline()));
        holder.tvStatus.setText(holder.itemView.getContext()
                .getString(R.string.label_status_value, task.getStatus()));
        holder.tvAssignedBy.setText(holder.itemView.getContext()
                .getString(R.string.label_assigned_by_value, task.getAssignedBy()));

        boolean canReport = "Employee".equalsIgnoreCase(userRole);
        holder.layoutActions.setVisibility(canReport ? View.VISIBLE : View.GONE);
        if (canReport) {
            holder.btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpdateStatus(task);
                }
            });
        } else {
            holder.btnUpdateStatus.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDeadline, tvStatus;
        TextView tvAssignedBy;
        View layoutActions;
        com.google.android.material.button.MaterialButton btnUpdateStatus;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAssignedBy = itemView.findViewById(R.id.tvAssignedBy);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}








