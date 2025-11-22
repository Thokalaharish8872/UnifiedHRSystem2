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
    private CardView cvAttendance, cvTasks, cvKRA, cvDailyReport, cvFeedback;
    private SharedPreferences prefs;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        auth = FirebaseHelper.getInstance().getAuth();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        cvFeedback = findViewById(R.id.cvFeedback);
    }

    private void setupClickListeners() {

        cvAttendance.setOnClickListener(v ->
                startActivity(new Intent(this, AttendanceActivity.class)));

        cvTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TaskManagementActivity.class)));

        cvKRA.setOnClickListener(v ->
                startActivity(new Intent(this, KRAListActivity.class)));

        cvDailyReport.setOnClickListener(v ->
                startActivity(new Intent(this, DailyReportActivity.class)));

        cvFeedback.setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeFeedbackActivity.class)));
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
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
