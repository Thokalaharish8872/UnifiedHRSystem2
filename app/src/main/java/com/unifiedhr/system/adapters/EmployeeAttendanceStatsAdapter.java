package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unifiedhr.system.R;
import com.unifiedhr.system.models.EmployeeAttendanceStats;

import java.util.List;

public class EmployeeAttendanceStatsAdapter extends RecyclerView.Adapter<EmployeeAttendanceStatsAdapter.ViewHolder> {

    private final List<EmployeeAttendanceStats> employeeStats;

    public EmployeeAttendanceStatsAdapter(List<EmployeeAttendanceStats> employeeStats) {
        this.employeeStats = employeeStats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee_attendance_stats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmployeeAttendanceStats stats = employeeStats.get(position);
        holder.tvEmployeeName.setText(stats.getEmployeeName());
        holder.tvEmployeeId.setText("Employee ID: " + stats.getEmployeeId());
        holder.tvDaysPresentThisWeek.setText("Present This Week: " + stats.getDaysPresentThisWeek());
        holder.tvDaysPresentThisMonth.setText("Present This Month: " + stats.getDaysPresentThisMonth());
        holder.tvDaysPresentThisYear.setText("Present This Year: " + stats.getDaysPresentThisYear());
    }

    @Override
    public int getItemCount() {
        return employeeStats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName;
        TextView tvEmployeeId;
        TextView tvDaysPresentThisWeek;
        TextView tvDaysPresentThisMonth;
        TextView tvDaysPresentThisYear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
            tvDaysPresentThisWeek = itemView.findViewById(R.id.tvDaysPresentThisWeek);
            tvDaysPresentThisMonth = itemView.findViewById(R.id.tvDaysPresentThisMonth);
            tvDaysPresentThisYear = itemView.findViewById(R.id.tvDaysPresentThisYear);
        }
    }
}
