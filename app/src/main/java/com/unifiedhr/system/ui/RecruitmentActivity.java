package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.JobAdapter;
import com.unifiedhr.system.models.Job;
import com.unifiedhr.system.services.RecruitmentService;
import com.unifiedhr.system.ui.fragments.CreateJobDialogFragment;
import com.unifiedhr.system.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RecruitmentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private JobAdapter adapter;
    private List<Job> jobList;
    private RecruitmentService recruitmentService;
    private Button btnCreateJob;
    private String companyId;
    private String userRole;
    private boolean isRecruiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        companyId = prefs.getString("companyId", "");
        userRole = prefs.getString("userRole", "");
        isRecruiter = prefs.getBoolean("isRecruiter", false) || userRole.equals("Admin") || userRole.equals("Manager");

        recruitmentService = new RecruitmentService();
        jobList = new ArrayList<>();

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        initViews();
        loadJobs();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobAdapter(jobList, isRecruiter, recruitmentService, this);
        recyclerView.setAdapter(adapter);

        btnCreateJob = findViewById(R.id.btnCreateJob);
        if (!isRecruiter) {
            btnCreateJob.setVisibility(View.GONE);
        }
        btnCreateJob.setOnClickListener(v -> showCreateJobDialog());
    }

    private void loadJobs() {
        recruitmentService.getJobsByCompany(companyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Job job = child.getValue(Job.class);
                    if (job != null) {
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

    private void showCreateJobDialog() {
        CreateJobDialogFragment dialog = new CreateJobDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateJob");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

