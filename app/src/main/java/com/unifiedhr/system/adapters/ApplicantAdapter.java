package com.unifiedhr.system.adapters;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Applicant;
import com.unifiedhr.system.ui.ApplicantsActivity;
import com.unifiedhr.system.ui.MessagingActivity;
import com.unifiedhr.system.services.RecruitmentService;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {
    private List<Applicant> applicantList;
    private ApplicantsActivity activity;
    private RecruitmentService recruitmentService;
    private String jobId;

    public ApplicantAdapter(List<Applicant> applicantList, ApplicantsActivity activity) {
        this.applicantList = applicantList;
        this.activity = activity;
        this.recruitmentService = new RecruitmentService();
        this.jobId = activity.getIntent().getStringExtra("jobId");
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        Applicant applicant = applicantList.get(position);
        holder.tvName.setText(applicant.getName());
        holder.tvEmail.setText(applicant.getEmail());
        holder.tvPhone.setText(applicant.getPhone() != null ? applicant.getPhone() : "Not provided");
        holder.tvStatus.setText("Status: " + applicant.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.tvAppliedDate.setText("Applied: " + sdf.format(new Date(applicant.getAppliedAt())));

        // Resume button - check for resumeData (Base64), resumeUrl (legacy), or hasResume flag
        // Also check if applicant has a resume by checking Firebase directly if needed
        boolean hasResume = (applicant.getResumeData() != null && !applicant.getResumeData().isEmpty()) ||
                           (applicant.getResumeUrl() != null && !applicant.getResumeUrl().isEmpty());
        
        // Always show resume button - if resume data isn't loaded, we'll fetch it on click
        // This ensures recruiters can always try to view resumes
        holder.btnViewResume.setVisibility(View.VISIBLE);
        holder.btnViewResume.setEnabled(true);
        holder.btnViewResume.setOnClickListener(v -> {
            if (applicant.getResumeData() != null && !applicant.getResumeData().isEmpty()) {
                // Decode Base64 and create temporary file
                viewResumeFromBase64(applicant.getResumeData(), applicant.getApplicantId());
            } else if (applicant.getResumeUrl() != null && !applicant.getResumeUrl().isEmpty()) {
                // Legacy: try to open URL
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(applicant.getResumeUrl()));
                    activity.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(activity, "Could not open resume URL", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Resume data might not be loaded, fetch it from Firebase
                fetchAndViewResume(applicant.getApplicantId());
            }
        });

        // Message button
        holder.btnMessage.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MessagingActivity.class);
            intent.putExtra("jobId", jobId);
            intent.putExtra("applicantId", applicant.getApplicantId());
            activity.startActivity(intent);
        });

        // Status update buttons
        holder.btnShortlist.setOnClickListener(v -> updateStatus(applicant.getApplicantId(), "Shortlisted"));
        holder.btnReject.setOnClickListener(v -> updateStatus(applicant.getApplicantId(), "Rejected"));
    }

    private void viewResumeFromBase64(String base64Data, String applicantId) {
        try {
            if (base64Data == null || base64Data.isEmpty()) {
                Toast.makeText(activity, "Resume data is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Decode Base64 to byte array
            byte[] pdfBytes = Base64.decode(base64Data, Base64.DEFAULT);
            
            if (pdfBytes == null || pdfBytes.length == 0) {
                Toast.makeText(activity, "Invalid resume data", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create temporary file in external files directory for better compatibility
            File tempFile = new File(activity.getExternalFilesDir(null), applicantId + "_resume.pdf");
            if (tempFile.exists()) {
                tempFile.delete();
            }
            
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(pdfBytes);
            fos.close();
            
            // Create URI using FileProvider for secure file sharing
            Uri fileUri = FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".fileprovider",
                tempFile
            );
            
            // Create intent to view PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            // Try to find a PDF viewer
            android.content.pm.ResolveInfo resolveInfo = activity.getPackageManager()
                .resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
            
            if (resolveInfo != null) {
                // Grant permissions to all potential PDF viewers
                java.util.List<android.content.pm.ResolveInfo> resolveInfoList = activity.getPackageManager()
                    .queryIntentActivities(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
                
                for (android.content.pm.ResolveInfo info : resolveInfoList) {
                    String packageName = info.activityInfo.packageName;
                    activity.grantUriPermission(packageName, fileUri, 
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                
                activity.startActivity(Intent.createChooser(intent, "Open PDF with"));
            } else {
                // Fallback: try with a more generic intent
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW);
                fallbackIntent.setDataAndType(fileUri, "*/*");
                fallbackIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                
                if (fallbackIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(Intent.createChooser(fallbackIntent, "Open file with"));
                } else {
                    Toast.makeText(activity, "No app found to open PDF. Please install a PDF reader app.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error opening resume: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAndViewResume(String applicantId) {
        // Show loading message
        Toast.makeText(activity, "Loading resume...", Toast.LENGTH_SHORT).show();

        recruitmentService.getApplicant(applicantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String resumeData = snapshot.child("resumeData").getValue(String.class);
                    if (resumeData != null && !resumeData.isEmpty()) {
                        viewResumeFromBase64(resumeData, applicantId);
                    } else {
                        Toast.makeText(activity, "Resume not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, "Applicant not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(activity, "Error loading resume: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String applicantId, String status) {
        recruitmentService.updateApplicantStatus(applicantId, status, (error, ref) -> {
            if (error == null) {
                Toast.makeText(activity, "Status updated to " + status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName, tvEmail, tvPhone, tvStatus, tvAppliedDate;
        Button btnViewResume, btnMessage, btnShortlist, btnReject;

        ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAppliedDate = itemView.findViewById(R.id.tvAppliedDate);
            btnViewResume = itemView.findViewById(R.id.btnViewResume);
            btnMessage = itemView.findViewById(R.id.btnMessage);
            btnShortlist = itemView.findViewById(R.id.btnShortlist);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}


