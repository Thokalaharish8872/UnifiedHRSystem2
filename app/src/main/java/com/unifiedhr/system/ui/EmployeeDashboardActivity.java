package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.unifiedhr.system.R;
import com.unifiedhr.system.utils.FirebaseHelper;

public class EmployeeDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome, tvEmployeeId;
    private CardView cvAttendance, cvTasks, cvKRA, cvDailyReport, cvLeave, cvExpenses, cvDocuments;
    private SharedPreferences prefs;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        auth = FirebaseHelper.getInstance().getAuth();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        initViews();
        setupClickListeners();
        loadUserInfo();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmployeeId = findViewById(R.id.tvEmployeeId);
        cvAttendance = findViewById(R.id.cvAttendance);
        cvTasks = findViewById(R.id.cvTasks);
        cvKRA = findViewById(R.id.cvKRA);
        cvDailyReport = findViewById(R.id.cvDailyReport);
        cvLeave = findViewById(R.id.cvLeave);
    }

    private void setupClickListeners() {
        cvAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendanceActivity.class);
            startActivity(intent);
        });

        cvTasks.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskManagementActivity.class);
            startActivity(intent);
        });

        cvKRA.setOnClickListener(v -> {
            Intent intent = new Intent(this, KRAActivity.class);
            startActivity(intent);
        });

        cvDailyReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, DailyReportActivity.class);
            startActivity(intent);
        });

        cvLeave.setOnClickListener(v -> {
            Intent intent = new Intent(this, LeaveRequestActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        String name = prefs.getString("userName", "Employee");
        String employeeId = prefs.getString("employeeId", "");
        tvWelcome.setText("Welcome, " + name);
        tvEmployeeId.setText("Employee ID: " + employeeId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employee_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        auth.signOut();
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}



