package com.unifiedhr.system.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Attendance;

import java.util.List;

public class AttendanceRequestAdapter extends RecyclerView.Adapter<AttendanceRequestAdapter.AttendanceViewHolder> {

    public interface AttendanceActionListener {
        void onApprove(Attendance attendance);
        void onReject(Attendance attendance);
    }

    public enum Mode {
        MANAGER,
        ADMIN
    }

    private final List<Attendance> requests;
    private final Mode mode;
    private final AttendanceActionListener listener;

    public AttendanceRequestAdapter(List<Attendance> requests, Mode mode, AttendanceActionListener listener) {
        this.requests = requests;
        this.mode = mode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_request, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance attendance = requests.get(position);

        Context context = holder.itemView.getContext();

        holder.tvEmployee.setText(context.getString(R.string.label_employee_id, attendance.getEmployeeId()));
        holder.tvDate.setText(context.getString(R.string.label_date_value, attendance.getDate()));

        int typeLabelRes = Attendance.TYPE_LEAVE.equals(attendance.getRequestType())
                ? R.string.attendance_type_leave
                : R.string.attendance_type_present;
        holder.tvType.setText(context.getString(R.string.label_request_type, context.getString(typeLabelRes)));

        String reason = attendance.getReason();
        holder.tvReason.setText(context.getString(R.string.label_reason_value,
                reason == null || reason.isEmpty()
                        ? context.getString(R.string.not_applicable)
                        : reason));

        holder.tvStatus.setText(context.getString(R.string.label_status_value,
                getStatusLabel(context, attendance.getStatus())));

        boolean showActions = shouldShowActions(attendance.getStatus());
        holder.layoutActions.setVisibility(showActions ? View.VISIBLE : View.GONE);

        if (showActions) {
            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApprove(attendance);
                }
            });
            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReject(attendance);
                }
            });
        } else {
            holder.btnApprove.setOnClickListener(null);
            holder.btnReject.setOnClickListener(null);
        }
    }

    private boolean shouldShowActions(String status) {
        if (mode == Mode.MANAGER) {
            return Attendance.STATUS_PENDING_MANAGER.equals(status);
        } else if (mode == Mode.ADMIN) {
            return Attendance.STATUS_PENDING_MANAGER.equals(status)
                    || Attendance.STATUS_PENDING_ADMIN.equals(status);
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private String getStatusLabel(Context context, String status) {
        switch (status) {
            case Attendance.STATUS_PENDING_MANAGER:
                return context.getString(R.string.status_manager_pending);
            case Attendance.STATUS_PENDING_ADMIN:
                return context.getString(R.string.status_admin_pending);
            case Attendance.STATUS_MANAGER_APPROVED:
                return context.getString(R.string.status_manager_approved);
            case Attendance.STATUS_MANAGER_REJECTED:
                return context.getString(R.string.status_manager_rejected);
            case Attendance.STATUS_ADMIN_APPROVED:
                return context.getString(R.string.status_admin_approved);
            case Attendance.STATUS_ADMIN_REJECTED:
                return context.getString(R.string.status_admin_rejected);
            default:
                return status;
        }
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployee;
        TextView tvDate;
        TextView tvType;
        TextView tvReason;
        TextView tvStatus;
        View layoutActions;
        MaterialButton btnApprove;
        MaterialButton btnReject;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployee = itemView.findViewById(R.id.tvEmployee);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}