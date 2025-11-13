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
    private CardView cvTeam, cvTasks, cvAttendance, cvPerformance, cvRecruitment;
    private SharedPreferences prefs;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

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
        cvTeam = findViewById(R.id.cvTeam);
        cvTasks = findViewById(R.id.cvTasks);
        cvAttendance = findViewById(R.id.cvAttendance);
        cvPerformance = findViewById(R.id.cvPerformance);
        cvRecruitment = findViewById(R.id.cvRecruitment);
    }

    private void setupClickListeners() {
        cvTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamManagementActivity.class);
            startActivity(intent);
        });

        cvTasks.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskManagementActivity.class);
            startActivity(intent);
        });

        cvAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendanceActivity.class);
            startActivity(intent);
        });

        cvPerformance.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerformanceActivity.class);
            startActivity(intent);
        });

        cvRecruitment.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecruitmentActivity.class);
            startActivity(intent);
        });
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}



