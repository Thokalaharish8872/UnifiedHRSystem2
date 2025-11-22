package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.EmployeeAttendanceStatsAdapter;
import com.unifiedhr.system.models.Attendance;
import com.unifiedhr.system.models.EmployeeAttendanceStats;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.utils.FirebaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceMonitoringActivity extends AppCompatActivity {

    private RecyclerView rvAttendanceStats;
    private EmployeeAttendanceStatsAdapter adapter;
    private List<EmployeeAttendanceStats> employeeStatsList;
    private DatabaseReference attendanceRef;
    private DatabaseReference usersRef;

    private String companyId;
    private String userId;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_monitoring);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Attendance Monitoring");

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        companyId = prefs.getString("companyId", "");
        userId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");

        rvAttendanceStats = findViewById(R.id.recyclerView);
        rvAttendanceStats.setLayoutManager(new LinearLayoutManager(this));

        employeeStatsList = new ArrayList<>();
        adapter = new EmployeeAttendanceStatsAdapter(employeeStatsList);
        rvAttendanceStats.setAdapter(adapter);

        attendanceRef = FirebaseHelper.getInstance().getDatabaseReference("attendance");
        usersRef = FirebaseHelper.getInstance().getDatabaseReference("users");

        adapter.setOnFeedbackClickListener(stats -> showFeedbackDialog(stats));

        if (TextUtils.isEmpty(companyId)) {
            Toast.makeText(this, "Company information not available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAttendanceData();
    }

    private void loadAttendanceData() {
        Query query;

        if ("Admin".equalsIgnoreCase(userRole)) {
            query = usersRef.orderByChild("companyId").equalTo(companyId);
        } else if ("Manager".equalsIgnoreCase(userRole)) {
            query = usersRef.orderByChild("managerId").equalTo(userId);
        } else {
            employeeStatsList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {

                Map<String, String> employeeNames = new HashMap<>();

                for (DataSnapshot userSnap : usersSnapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user == null) continue;

                    boolean shouldInclude = false;

                    if ("Admin".equalsIgnoreCase(userRole)) {
                        if (!userId.equals(user.getUserId()) &&
                                ("Employee".equalsIgnoreCase(user.getRole()) ||
                                        "Manager".equalsIgnoreCase(user.getRole()))) {
                            shouldInclude = true;
                        }
                    } else if ("Manager".equalsIgnoreCase(userRole)) {
                        if ("Employee".equalsIgnoreCase(user.getRole())) {
                            shouldInclude = true;
                        }
                    }

                    if (shouldInclude) {
                        employeeNames.put(user.getUserId(), user.getName());
                    }
                }

                if (employeeNames.isEmpty()) {
                    employeeStatsList.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }

                attendanceRef.orderByChild("companyId").equalTo(companyId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {

                        Map<String, EmployeeAttendanceStats> statsMap = new HashMap<>();

                        for (Map.Entry<String, String> entry : employeeNames.entrySet()) {
                            statsMap.put(entry.getKey(),
                                    new EmployeeAttendanceStats(entry.getKey(), entry.getValue()));
                        }

                        for (DataSnapshot snapshot : attendanceSnapshot.getChildren()) {

                            Attendance attendance = snapshot.getValue(Attendance.class);
                            if (attendance == null) continue;

                            if (!employeeNames.containsKey(attendance.getEmployeeId())) continue;

                            if (Attendance.STATUS_APPROVED_MANAGER.equals(attendance.getStatus()) ||
                                    Attendance.STATUS_APPROVED_ADMIN.equals(attendance.getStatus())) {

                                String empId = attendance.getEmployeeId();
                                EmployeeAttendanceStats stats = statsMap.get(empId);

                                if (stats == null) continue;

                                try {
                                    Date attDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            .parse(attendance.getDate());

                                    if (isDateInCurrentWeek(attDate))
                                        stats.setDaysPresentThisWeek(stats.getDaysPresentThisWeek() + 1);

                                    if (isDateInCurrentMonth(attDate))
                                        stats.setDaysPresentThisMonth(stats.getDaysPresentThisMonth() + 1);

                                    if (isDateInCurrentYear(attDate))
                                        stats.setDaysPresentThisYear(stats.getDaysPresentThisYear() + 1);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        employeeStatsList.clear();
                        employeeStatsList.addAll(statsMap.values());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AttendanceMonitoringActivity.this,
                                "Failed to load attendance data.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendanceMonitoringActivity.this,
                        "Failed to load employee data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showFeedbackDialog(EmployeeAttendanceStats stats) {

        View view = getLayoutInflater().inflate(R.layout.dialog_feedback, null);

        TextInputLayout layout = view.findViewById(R.id.inputLayoutFeedback);
        TextInputEditText etFeedback = view.findViewById(R.id.etFeedback);

        String summary =
                "Weekly: " + stats.getDaysPresentThisWeek() + " days\n" +
                        "Monthly: " + stats.getDaysPresentThisMonth() + " days\n" +
                        "Yearly: " + stats.getDaysPresentThisYear() + " days\n\n";

        etFeedback.setText(summary);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Send Feedback to " + stats.getEmployeeName())
                .setView(view)
                .setPositiveButton("Send", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btn.setOnClickListener(v -> {
                String message = etFeedback.getText().toString().trim();

                if (message.isEmpty()) {
                    layout.setError("Feedback required");
                    return;
                }

                sendFeedbackToFirebase(stats, message);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void sendFeedbackToFirebase(EmployeeAttendanceStats stats, String message) {

        DatabaseReference ref = FirebaseHelper.getInstance()
                .getDatabaseReference("feedback")
                .child(stats.getEmployeeId());

        String feedbackId = ref.push().getKey();

        Map<String, Object> data = new HashMap<>();
        data.put("feedbackId", feedbackId);
        data.put("employeeId", stats.getEmployeeId());
        data.put("employeeName", stats.getEmployeeName());
        data.put("message", message);
        data.put("sentBy", userId);
        data.put("timestamp", System.currentTimeMillis());

        ref.child(feedbackId).setValue(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Feedback Sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to send feedback.", Toast.LENGTH_SHORT).show());
    }


    private boolean isDateInCurrentWeek(Date date) {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return current.get(Calendar.WEEK_OF_YEAR) == target.get(Calendar.WEEK_OF_YEAR)
                && current.get(Calendar.YEAR) == target.get(Calendar.YEAR);
    }

    private boolean isDateInCurrentMonth(Date date) {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return current.get(Calendar.MONTH) == target.get(Calendar.MONTH)
                && current.get(Calendar.YEAR) == target.get(Calendar.YEAR);
    }

    private boolean isDateInCurrentYear(Date date) {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return current.get(Calendar.YEAR) == target.get(Calendar.YEAR);
    }
}
