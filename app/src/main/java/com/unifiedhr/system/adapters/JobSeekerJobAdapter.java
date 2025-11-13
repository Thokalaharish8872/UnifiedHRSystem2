package com.unifiedhr.system.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Job;
import com.unifiedhr.system.ui.JobSeekerDashboardActivity;

import java.util.List;

public class JobSeekerJobAdapter extends RecyclerView.Adapter<JobSeekerJobAdapter.JobViewHolder> {
    private List<Job> jobList;
    private JobSeekerDashboardActivity activity;

    public JobSeekerJobAdapter(List<Job> jobList, JobSeekerDashboardActivity activity) {
        this.jobList = jobList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jobseeker_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvTitle.setText(job.getTitle());
        holder.tvDescription.setText(job.getDescription());
        holder.tvDepartment.setText(job.getDepartment() != null ? job.getDepartment() : "Not specified");
        holder.tvLocation.setText(job.getLocation() != null ? job.getLocation() : "Not specified");
        holder.tvApplicants.setText(job.getApplicantCount() + " applicants");

        holder.btnApply.setOnClickListener(v -> {
            Intent intent = new Intent(activity, com.unifiedhr.system.ui.JobDetailActivity.class);
            intent.putExtra("jobId", job.getJobId());
            activity.startActivity(intent);
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(activity, com.unifiedhr.system.ui.JobDetailActivity.class);
            intent.putExtra("jobId", job.getJobId());
            activity.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvDescription, tvDepartment, tvLocation, tvApplicants;
        Button btnApply, btnViewDetails;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvApplicants = itemView.findViewById(R.id.tvApplicants);
            btnApply = itemView.findViewById(R.id.btnApply);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}

