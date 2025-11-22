package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unifiedhr.system.R;
import com.unifiedhr.system.models.EmployeeAttendanceStats;

import java.util.List;

public class EmployeeAttendanceStatsAdapter extends RecyclerView.Adapter<EmployeeAttendanceStatsAdapter.ViewHolder> {

    public interface OnFeedbackClickListener {
        void onFeedbackClick(EmployeeAttendanceStats stats);
    }

    private final List<EmployeeAttendanceStats> employeeStats;
    private OnFeedbackClickListener feedbackListener;

    public EmployeeAttendanceStatsAdapter(List<EmployeeAttendanceStats> employeeStats) {
        this.employeeStats = employeeStats;
    }

    public void setOnFeedbackClickListener(OnFeedbackClickListener listener) {
        this.feedbackListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee_attendance_stats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmployeeAttendanceStats stats = employeeStats.get(position);

        holder.tvEmployeeName.setText(stats.getEmployeeName());
        holder.tvEmployeeId.setText("Employee ID: " + stats.getEmployeeId());
        holder.tvWeek.setText("Present This Week: " + stats.getDaysPresentThisWeek());
        holder.tvMonth.setText("Present This Month: " + stats.getDaysPresentThisMonth());
        holder.tvYear.setText("Present This Year: " + stats.getDaysPresentThisYear());

        holder.btnFeedback.setOnClickListener(v -> {
            if (feedbackListener != null)
                feedbackListener.onFeedbackClick(stats);
        });
    }

    @Override
    public int getItemCount() {
        return employeeStats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvEmployeeId, tvWeek, tvMonth, tvYear;
        Button btnFeedback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
            tvWeek = itemView.findViewById(R.id.tvDaysPresentThisWeek);
            tvMonth = itemView.findViewById(R.id.tvDaysPresentThisMonth);
            tvYear = itemView.findViewById(R.id.tvDaysPresentThisYear);
            btnFeedback = itemView.findViewById(R.id.btnSendFeedback);
        }
    }
}
