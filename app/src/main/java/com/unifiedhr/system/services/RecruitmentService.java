package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.Applicant;
import com.unifiedhr.system.models.Job;
import com.unifiedhr.system.utils.FirebaseHelper;

public class RecruitmentService {
    private DatabaseReference jobsRef;
    private DatabaseReference applicantsRef;

    public RecruitmentService() {
        jobsRef = FirebaseHelper.getInstance().getDatabaseReference("jobs");
        applicantsRef = FirebaseHelper.getInstance().getDatabaseReference("applicants");
    }

    public void createJob(Job job, DatabaseReference.CompletionListener listener) {
        jobsRef.child(job.getJobId()).setValue(job, listener);
    }

    public DatabaseReference getJob(String jobId) {
        return jobsRef.child(jobId);
    }

    public Query getJobsByCompany(String companyId) {
        return jobsRef.orderByChild("companyId").equalTo(companyId);
    }

    public void addApplicant(Applicant applicant, DatabaseReference.CompletionListener listener) {
        applicantsRef.child(applicant.getApplicantId()).setValue(applicant, listener);
    }

    public Query getApplicantsByJob(String jobId) {
        return applicantsRef.orderByChild("jobId").equalTo(jobId);
    }

    public void updateApplicantStatus(String applicantId, String status, DatabaseReference.CompletionListener listener) {
        applicantsRef.child(applicantId).child("status").setValue(status, listener);
    }

    public DatabaseReference getApplicant(String applicantId) {
        return applicantsRef.child(applicantId);
    }

    public Query getAllActiveJobs() {
        return jobsRef.orderByChild("status").equalTo("Active");
    }
}

