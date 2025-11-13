package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.JobSeekerJobAdapter;
import com.unifiedhr.system.models.Job;
import com.unifiedhr.system.services.RecruitmentService;
import com.unifiedhr.system.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class JobSeekerDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private RecyclerView recyclerView;
    private JobSeekerJobAdapter adapter;
    private List<Job> jobList;
    private RecruitmentService recruitmentService;
    private SharedPreferences prefs;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobseeker_dashboard);

        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        auth = FirebaseHelper.getInstance().getAuth();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        recruitmentService = new RecruitmentService();
        jobList = new ArrayList<>();

        initViews();
        loadUserInfo();
        loadAllJobs();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobSeekerJobAdapter(jobList, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadUserInfo() {
        String name = prefs.getString("userName", "Job Seeker");
        tvWelcome.setText("Welcome, " + name);
    }

    private void loadAllJobs() {
        // Load all active jobs from all companies
        recruitmentService.getAllActiveJobs().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Job job = child.getValue(Job.class);
                    if (job != null && "Active".equals(job.getStatus())) {
                        jobList.add(job);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.jobseeker_menu, menu);
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






