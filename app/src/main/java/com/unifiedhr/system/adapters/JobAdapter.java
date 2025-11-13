package com.unifiedhr.system.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Job;
import com.unifiedhr.system.services.RecruitmentService;
import com.unifiedhr.system.ui.ApplicantsActivity;
import com.unifiedhr.system.ui.RecruitmentActivity;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<Job> jobList;
    private boolean isRecruiter;
    private RecruitmentService recruitmentService;
    private RecruitmentActivity activity;

    public JobAdapter(List<Job> jobList, boolean isRecruiter, RecruitmentService recruitmentService, RecruitmentActivity activity) {
        this.jobList = jobList;
        this.isRecruiter = isRecruiter;
        this.recruitmentService = recruitmentService;
        this.activity = activity;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvTitle.setText(job.getTitle());
        holder.tvDescription.setText(job.getDescription());
        holder.tvApplicants.setText("Applicants: " + job.getApplicantCount());

        if (isRecruiter) {
            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ApplicantsActivity.class);
                intent.putExtra("jobId", job.getJobId());
                activity.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvDescription, tvApplicants;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvApplicants = itemView.findViewById(R.id.tvApplicants);
        }
    }
}



