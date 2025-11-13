package com.unifiedhr.system.ui;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Applicant;
import com.unifiedhr.system.models.Job;
import com.unifiedhr.system.services.RecruitmentService;
import com.unifiedhr.system.utils.FirebaseHelper;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import android.util.Base64;

public class JobDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvDescription, tvDepartment, tvLocation, tvSkills, tvApplicants;
    private Button btnApply, btnViewMessages;
    private Job job;
    private RecruitmentService recruitmentService;
    private String jobId;
    private String userId;
    private String applicantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        applicantId = userId + "_" + jobId;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recruitmentService = new RecruitmentService();
        initViews();
        loadJobDetails();
        checkApplicationStatus();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvLocation = findViewById(R.id.tvLocation);
        tvSkills = findViewById(R.id.tvSkills);
        tvApplicants = findViewById(R.id.tvApplicants);
        btnApply = findViewById(R.id.btnApply);
        btnViewMessages = findViewById(R.id.btnViewMessages);

        btnApply.setOnClickListener(v -> applyForJob());
        btnViewMessages.setOnClickListener(v -> {
            Intent intent = new Intent(this, MessagingActivity.class);
            intent.putExtra("jobId", jobId);
            intent.putExtra("applicantId", applicantId);
            startActivity(intent);
        });
    }

    private void loadJobDetails() {
        recruitmentService.getJob(jobId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                job = snapshot.getValue(Job.class);
                if (job != null) {
                    displayJobDetails();
                } else {
                    Toast.makeText(JobDetailActivity.this, "Job not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(JobDetailActivity.this, "Error loading job", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayJobDetails() {
        tvTitle.setText(job.getTitle());
        tvDescription.setText(job.getDescription());
        tvDepartment.setText(job.getDepartment() != null ? job.getDepartment() : "Not specified");
        tvLocation.setText(job.getLocation() != null ? job.getLocation() : "Not specified");
        tvApplicants.setText(job.getApplicantCount() + " applicants");

        View cvSkills = findViewById(R.id.cvSkills);
        if (job.getSkillsRequired() != null && !job.getSkillsRequired().isEmpty()) {
            cvSkills.setVisibility(View.VISIBLE);
            List<String> skills = Arrays.asList(job.getSkillsRequired().split(","));
            StringBuilder skillsText = new StringBuilder();
            for (String skill : skills) {
                skillsText.append("• ").append(skill.trim()).append("\n");
            }
            tvSkills.setText(skillsText.toString());
        } else {
            cvSkills.setVisibility(View.GONE);
        }
    }

    private void checkApplicationStatus() {
        recruitmentService.getApplicant(applicantId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Applicant applicant = snapshot.getValue(Applicant.class);
                    if (applicant != null) {
                        btnApply.setText("Applied - " + applicant.getStatus());
                        btnApply.setEnabled(false);
                        btnViewMessages.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void applyForJob() {
        if (userId == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        com.unifiedhr.system.ui.fragments.ApplyJobDialogFragment dialog =
                new com.unifiedhr.system.ui.fragments.ApplyJobDialogFragment();
        dialog.setOnApplyListener((phone, resumeUri) -> submitApplication(phone, resumeUri));
        dialog.show(getSupportFragmentManager(), "ApplyJob");
    }

    private void submitApplication(String phone, Uri resumeUri) {
        String email = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getEmail() : "";
        String name = email.contains("@") ? email.substring(0, email.indexOf("@")) : "Job Seeker";

        Applicant applicant = new Applicant(applicantId, jobId, name, email);
        applicant.setPhone(phone);
        applicant.setAppliedAt(System.currentTimeMillis());

        recruitmentService.addApplicant(applicant, (error, ref) -> {
            if (error == null) {
                if (resumeUri != null) {
                    uploadResumeToFirebase(resumeUri, applicantId);
                } else {
                    onApplicationSuccess();
                }
            } else {
                Toast.makeText(this, "Failed to submit application", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadResumeToFirebase(Uri fileUri, String applicantId) {
        try {
            // Read PDF file and convert to Base64
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                Toast.makeText(this, "Could not read file", Toast.LENGTH_SHORT).show();
                return;
            }

            // Read all bytes from input stream (handle large files)
            byte[] buffer = new byte[8192];
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            byte[] pdfBytes = baos.toByteArray();

            // Convert to Base64 string
            String base64Resume = Base64.encodeToString(pdfBytes, Base64.DEFAULT);

            // Check size limit (Realtime Database has 256MB limit per node, Base64 increases size by ~33%)
            // For safety, limit to ~150MB of original file (200MB Base64)
            if (base64Resume.length() > 200 * 1024 * 1024) {
                Toast.makeText(this, "Resume file is too large. Please use a smaller file.", Toast.LENGTH_LONG).show();
                return;
            }

            // Store Base64 string in Realtime Database
            recruitmentService.getApplicant(applicantId).child("resumeData")
                    .setValue(base64Resume, (error, ref) -> {
                        if (error == null) {
                            // Also set a flag to indicate resume exists
                            recruitmentService.getApplicant(applicantId).child("hasResume")
                                    .setValue(true, (error2, ref2) -> {
                                        onApplicationSuccess();
                                    });
                        } else {
                            Toast.makeText(this, "Failed to save resume: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "File access error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void onApplicationSuccess() {
        Toast.makeText(this, "Application submitted successfully!", Toast.LENGTH_SHORT).show();
        btnApply.setText("Applied - New");
        btnApply.setEnabled(false);
        btnViewMessages.setVisibility(View.VISIBLE);

        job.setApplicantCount(job.getApplicantCount() + 1);
        recruitmentService.createJob(job, null);

        checkApplicationStatus();
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
