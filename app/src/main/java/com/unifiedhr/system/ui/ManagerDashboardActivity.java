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

public class ManagerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private CardView cvTeam, cvTasks, cvAttendance, cvRecruitment, cvAttendanceMonitoring, cvKRA, cvAttendanceRequests;
    private SharedPreferences prefs;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

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
        cvTeam = findViewById(R.id.cvTeam);
        cvTasks = findViewById(R.id.cvTasks);
        cvAttendance = findViewById(R.id.cvAttendance);
        cvRecruitment = findViewById(R.id.cvRecruitment);
        cvAttendanceMonitoring = findViewById(R.id.cvAttendanceMonitoring);
        cvKRA = findViewById(R.id.cvKRA);
        cvAttendanceRequests = findViewById(R.id.cvAttendanceRequests);
    }

    private void setupClickListeners() {
        cvTeam.setOnClickListener(v -> startActivity(new Intent(this, TeamManagementActivity.class)));
        cvTasks.setOnClickListener(v -> startActivity(new Intent(this, TaskManagementActivity.class)));
        cvAttendance.setOnClickListener(v -> startActivity(new Intent(this, AttendanceActivity.class)));
        cvRecruitment.setOnClickListener(v -> startActivity(new Intent(this, RecruitmentActivity.class)));
        cvAttendanceMonitoring.setOnClickListener(v -> startActivity(new Intent(this, AttendanceMonitoringActivity.class)));
        cvAttendanceRequests.setOnClickListener(v -> startActivity(new Intent(this, ManagerAttendanceRequestsActivity.class)));

        cvKRA.setOnClickListener(v ->
                startActivity(new Intent(this, ManagerCombinedKRAActivity.class))
        );
    }

    private void loadUserInfo() {
        String name = prefs.getString("userName", "Manager");
        tvWelcome.setText("Welcome, " + name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_menu, menu);
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
