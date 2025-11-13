package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.ApplicantAdapter;
import com.unifiedhr.system.models.Applicant;
import com.unifiedhr.system.services.RecruitmentService;

import java.util.ArrayList;
import java.util.List;

public class ApplicantsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApplicantAdapter adapter;
    private List<Applicant> applicantList;
    private RecruitmentService recruitmentService;
    private String jobId;
    private TextView tvJobTitle, tvNoApplicants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants);

        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Applicants");
        }

        recruitmentService = new RecruitmentService();
        applicantList = new ArrayList<>();

        initViews();
        loadApplicants();
    }

    private void initViews() {
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvNoApplicants = findViewById(R.id.tvNoApplicants);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicantAdapter(applicantList, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadApplicants() {
        recruitmentService.getApplicantsByJob(jobId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                applicantList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Applicant applicant = child.getValue(Applicant.class);
                    if (applicant != null) {
                        applicantList.add(applicant);
                    }
                }
                adapter.notifyDataSetChanged();
                
                if (applicantList.isEmpty()) {
                    tvNoApplicants.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoApplicants.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ApplicantsActivity.this, "Error loading applicants", Toast.LENGTH_SHORT).show();
            }
        });
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






