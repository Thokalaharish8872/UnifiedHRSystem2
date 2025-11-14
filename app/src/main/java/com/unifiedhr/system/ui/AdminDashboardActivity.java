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
import com.unifiedhr.system.ui.fragments.CreateCompanyDialogFragment;
import com.unifiedhr.system.utils.FirebaseHelper;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private CardView cvCreateCompany, cvManagers, cvAttendance, cvTeam, cvRecruitment;
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        initViews();
        setupClickListeners();
        loadUserInfo();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        cvCreateCompany = findViewById(R.id.cvCreateCompany);
        cvManagers = findViewById(R.id.cvManagers);
        cvAttendance = findViewById(R.id.cvAttendance);
        cvTeam = findViewById(R.id.cvTeam);
        cvRecruitment = findViewById(R.id.cvRecruitment);
    }

    private void setupClickListeners() {
        cvCreateCompany.setOnClickListener(v -> {
            CreateCompanyDialogFragment dialog = new CreateCompanyDialogFragment();
            dialog.show(getSupportFragmentManager(), "CreateCompany");
        });

        cvManagers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagerManagementActivity.class);
            startActivity(intent);
        });

        cvAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAttendanceRequestsActivity.class);
            startActivity(intent);
        });

        cvTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamManagementActivity.class);
            startActivity(intent);
        });

        cvRecruitment.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecruitmentActivity.class);
            startActivity(intent);
        });
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
