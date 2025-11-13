package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Performance;
import com.unifiedhr.system.services.PerformanceService;

import java.util.ArrayList;
import java.util.List;

public class PerformanceActivity extends AppCompatActivity {
    private TextView tvAttendance, tvCompletion, tvRating;
    private PerformanceService performanceService;
    private String employeeId;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        employeeId = prefs.getString("employeeId", "");
        userRole = prefs.getString("userRole", "");

        performanceService = new PerformanceService();

        initViews();
        loadPerformance();
    }

    private void initViews() {
        tvAttendance = findViewById(R.id.tvAttendance);
        tvCompletion = findViewById(R.id.tvCompletion);
        tvRating = findViewById(R.id.tvRating);
    }

    private void loadPerformance() {
        performanceService.getPerformanceByEmployee(employeeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Performance> performances = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Performance perf = child.getValue(Performance.class);
                    if (perf != null) {
                        performances.add(perf);
                    }
                }
                
                if (!performances.isEmpty()) {
                    // Get latest performance
                    Performance latest = performances.get(performances.size() - 1);
                    tvAttendance.setText("Attendance: " + latest.getAttendancePercentage() + "%");
                    tvCompletion.setText("Completion: " + latest.getTaskCompletionPercentage() + "%");
                    tvRating.setText("Rating: " + latest.getRating() + "/5");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}








