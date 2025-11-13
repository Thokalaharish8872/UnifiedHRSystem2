package com.unifiedhr.system.models;

public class Job {
    private String jobId;
    private String companyId;
    private String title;
    private String description;
    private String department;
    private String location;
    private String jobLink;
    private String status; // Active, Closed
    private String createdBy;
    private long createdAt;
    private int applicantCount;
    private String skillsRequired; // Comma-separated skills

    public Job() {
    }

    public Job(String jobId, String companyId, String title, String description, String createdBy) {
        this.jobId = jobId;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.status = "Active";
        this.createdAt = System.currentTimeMillis();
        this.applicantCount = 0;
    }

    // Getters and Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobLink() { return jobLink; }
    public void setJobLink(String jobLink) { this.jobLink = jobLink; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getApplicantCount() { return applicantCount; }
    public void setApplicantCount(int applicantCount) { this.applicantCount = applicantCount; }

    public String getSkillsRequired() { return skillsRequired; }
    public void setSkillsRequired(String skillsRequired) { this.skillsRequired = skillsRequired; }
}



