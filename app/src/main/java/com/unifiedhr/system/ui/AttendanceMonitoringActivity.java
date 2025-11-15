package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        rvAttendanceStats = findViewById(R.id.rvAttendance);
        rvAttendanceStats.setLayoutManager(new LinearLayoutManager(this));

        employeeStatsList = new ArrayList<>();
        adapter = new EmployeeAttendanceStatsAdapter(employeeStatsList);
        rvAttendanceStats.setAdapter(adapter);

        attendanceRef = FirebaseHelper.getInstance().getDatabaseReference("attendance");
        usersRef = FirebaseHelper.getInstance().getDatabaseReference("users");

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
                        if (!userId.equals(user.getUserId()) && ("Employee".equalsIgnoreCase(user.getRole()) || "Manager".equalsIgnoreCase(user.getRole()))) {
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

                attendanceRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                        Map<String, EmployeeAttendanceStats> statsMap = new HashMap<>();
                        for (Map.Entry<String, String> entry : employeeNames.entrySet()) {
                            statsMap.put(entry.getKey(), new EmployeeAttendanceStats(entry.getKey(), entry.getValue()));
                        }

                        for (DataSnapshot snapshot : attendanceSnapshot.getChildren()) {
                            Attendance attendance = snapshot.getValue(Attendance.class);
                            if (attendance != null && employeeNames.containsKey(attendance.getEmployeeId())) {
                                if (Attendance.STATUS_MANAGER_APPROVED.equals(attendance.getStatus()) || Attendance.STATUS_ADMIN_APPROVED.equals(attendance.getStatus())) {
                                    String employeeId = attendance.getEmployeeId();
                                    EmployeeAttendanceStats stats = statsMap.get(employeeId);
                                    if (stats == null) continue;

                                    try {
                                        Date attendanceDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(attendance.getDate());
                                        if (isDateInCurrentWeek(attendanceDate)) {
                                            stats.setDaysPresentThisWeek(stats.getDaysPresentThisWeek() + 1);
                                        }
                                        if (isDateInCurrentMonth(attendanceDate)) {
                                            stats.setDaysPresentThisMonth(stats.getDaysPresentThisMonth() + 1);
                                        }
                                        if (isDateInCurrentYear(attendanceDate)) {
                                            stats.setDaysPresentThisYear(stats.getDaysPresentThisYear() + 1);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        employeeStatsList.clear();
                        employeeStatsList.addAll(statsMap.values());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AttendanceMonitoringActivity.this, "Failed to load attendance data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AttendanceMonitoringActivity.this, "Failed to load employee data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return currentWeek == targetWeek && currentYear == targetYear;
    }

    private boolean isDateInCurrentMonth(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetMonth = targetCalendar.get(Calendar.MONTH);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return currentMonth == targetMonth && currentYear == targetYear;
    }

    private boolean isDateInCurrentYear(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return currentYear == targetYear;
    }
}
