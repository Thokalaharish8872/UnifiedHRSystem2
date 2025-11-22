package com.unifiedhr.system.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final String userRole;
    private final String currentUserId;
    private final TaskActionListener listener;

    public interface TaskActionListener {
        void onUpdateStatus(Task task);
        void onCreateSubtask(Task parentTask);
    }

    public TaskAdapter(List<Task> taskList, String userRole, String currentUserId,
                       TaskActionListener listener) {

        this.taskList = taskList;
        this.userRole = userRole;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvDeadline.setText("Deadline: " + task.getDeadline());
        holder.tvStatus.setText("Status: " + task.getStatus());
        holder.tvAssignedBy.setText("Assigned By: " + task.getAssignedBy());

        boolean isAdmin = "Admin".equalsIgnoreCase(userRole);
        boolean isManager = "Manager".equalsIgnoreCase(userRole);
        boolean isEmployee = "Employee".equalsIgnoreCase(userRole);

        if (isAdmin) {
            holder.layoutActions.setVisibility(View.GONE);
            return;
        }

        holder.layoutActions.setVisibility(View.VISIBLE);

        if (isManager && TextUtils.isEmpty(task.getParentTaskId())) {
            holder.btnCreateSubtask.setVisibility(View.VISIBLE);
            holder.btnCreateSubtask.setOnClickListener(v -> {
                if (listener != null) listener.onCreateSubtask(task);
            });
        } else {
            holder.btnCreateSubtask.setVisibility(View.GONE);
        }

        boolean canUpdateStatus =
                task.getAssignedTo() != null &&
                        task.getAssignedTo().equals(currentUserId) &&
                        (isManager || isEmployee);

        if (canUpdateStatus) {
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null) listener.onUpdateStatus(task);
            });
        } else {
            holder.btnUpdateStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvDeadline, tvStatus, tvAssignedBy;
        View layoutActions;
        MaterialButton btnUpdateStatus, btnCreateSubtask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAssignedBy = itemView.findViewById(R.id.tvAssignedBy);

            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnCreateSubtask = itemView.findViewById(R.id.btnCreateSubtask);
        }
    }
}
