package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseError;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.DailyReport;
import com.unifiedhr.system.services.DailyReportService;
import com.unifiedhr.system.utils.Utils;

public class DailyReportActivity extends AppCompatActivity {
    private EditText etWorkDone, etChallenges, etNextDayPlan;
    private Button btnSubmit;
    private DailyReportService reportService;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        employeeId = prefs.getString("employeeId", "");

        reportService = new DailyReportService();

        initViews();
    }

    private void initViews() {
        etWorkDone = findViewById(R.id.etWorkDone);
        etChallenges = findViewById(R.id.etChallenges);
        etNextDayPlan = findViewById(R.id.etNextDayPlan);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String workDone = etWorkDone.getText().toString().trim();
        String challenges = etChallenges.getText().toString().trim();
        String nextDayPlan = etNextDayPlan.getText().toString().trim();

        if (workDone.isEmpty()) {
            Toast.makeText(this, "Please enter work done", Toast.LENGTH_SHORT).show();
            return;
        }

        DailyReport report = new DailyReport();
        report.setReportId(Utils.generateId());
        report.setEmployeeId(employeeId);
        report.setDate(Utils.getCurrentDate());
        report.setWorkDone(workDone);
        report.setChallenges(challenges);
        report.setNextDayPlan(nextDayPlan);

        reportService.createDailyReport(report, (error, ref) -> {
            if (error == null) {
                Toast.makeText(this, "Daily report submitted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to submit report", Toast.LENGTH_SHORT).show();
            }
        });
    }
}








