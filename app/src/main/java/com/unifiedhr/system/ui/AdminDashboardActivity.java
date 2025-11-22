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

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private CardView cvManagers, cvTasks, cvTeam, cvRecruitment, cvAttendance, cvAttendanceMonitoring, cvKRA;
    private SharedPreferences prefs;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        auth = FirebaseHelper.getInstance().getAuth();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(false); }

        initViews();
        setupClickListeners();
        loadUserInfo();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        cvManagers = findViewById(R.id.cvManagers);
        cvTasks = findViewById(R.id.cvTasks);
        cvTeam = findViewById(R.id.cvTeam);
        cvRecruitment = findViewById(R.id.cvRecruitment);
        cvAttendance = findViewById(R.id.cvAttendance);
        cvAttendanceMonitoring = findViewById(R.id.cvAttendanceMonitoring);
        cvKRA = findViewById(R.id.cvKRA); // make sure id exists in XML
    }

    private void setupClickListeners() {
        cvManagers.setOnClickListener(v -> startActivity(new Intent(this, ManagerManagementActivity.class)));
        cvTasks.setOnClickListener(v -> startActivity(new Intent(this, TaskManagementActivity.class)));
        cvAttendance.setOnClickListener(v -> startActivity(new Intent(this, AdminAttendanceRequestsActivity.class)));
        cvTeam.setOnClickListener(v -> startActivity(new Intent(this, TeamManagementActivity.class)));
        cvRecruitment.setOnClickListener(v -> startActivity(new Intent(this, RecruitmentActivity.class)));
        cvAttendanceMonitoring.setOnClickListener(v -> startActivity(new Intent(this, AttendanceMonitoringActivity.class)));

        cvKRA.setOnClickListener(v -> startActivity(new Intent(this, KRAListActivity.class)));
    }

    private void loadUserInfo() {
        String name = prefs.getString("userName", "Admin");
        tvWelcome.setText("Welcome, " + name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
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
