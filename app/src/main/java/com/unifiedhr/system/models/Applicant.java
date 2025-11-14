package com.unifiedhr.system.models;

public class Applicant {
    private String applicantId;
    private String jobId;
    private String name;
    private String email;
    private String phone;
    private String resumeUrl;
    private String resumeData;
    private String status;
    private long appliedAt;
    private String notes;

    public Applicant() {
    }

    public Applicant(String applicantId, String jobId, String name, String email) {
        this.applicantId = applicantId;
        this.jobId = jobId;
        this.name = name;
        this.email = email;
        this.status = "New";
        this.appliedAt = System.currentTimeMillis();
    }

    public String getApplicantId() { return applicantId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getResumeUrl() { return resumeUrl; }

    public String getResumeData() { return resumeData; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getAppliedAt() { return appliedAt; }
    public void setAppliedAt(long appliedAt) { this.appliedAt = appliedAt; }
}




