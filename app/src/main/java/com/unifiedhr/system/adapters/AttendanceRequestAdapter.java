package com.unifiedhr.system.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Attendance;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.UserService;

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
    private final UserService userService;

    public AttendanceRequestAdapter(List<Attendance> requests, Mode mode, AttendanceActionListener listener) {
        this.requests = requests;
        this.mode = mode;
        this.listener = listener;
        this.userService = new UserService();
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
        userService.getUser(attendance.getEmployeeId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    holder.tvEmployeeName.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.tvEmployee.setText("Employee: " + attendance.getEmployeeId());
        holder.tvDate.setText("Date: " + attendance.getDate());

        holder.tvType.setText("Type: " + attendance.getRequestType());
        holder.tvReason.setText("Reason: " + attendance.getReason());

        holder.tvStatus.setText("Status: " + attendance.getStatus());

        boolean showActions = attendance.getStatus().equals(Attendance.STATUS_PENDING);

        holder.layoutActions.setVisibility(showActions ? View.VISIBLE : View.GONE);

        if (showActions) {
            holder.btnApprove.setOnClickListener(v -> listener.onApprove(attendance));
            holder.btnReject.setOnClickListener(v -> listener.onReject(attendance));
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployee, tvDate, tvType, tvReason, tvStatus, tvEmployeeName;
        View layoutActions;
        MaterialButton btnApprove, btnReject;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmployee = itemView.findViewById(R.id.tvEmployee);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);

            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
