package com.unifiedhr.system.models;

public class KRA {

    private String kraId, companyId, employeeId, assignedBy, title, description, target, deadline, currentProgress, status;

    public KRA() {}

    public KRA(String kraId, String companyId, String employeeId,
               String title, String description, String target, String deadline) {

        this.kraId = kraId;
        this.companyId = companyId;
        this.employeeId = employeeId;

        this.title = title;
        this.description = description;
        this.target = target;
        this.deadline = deadline;

        this.currentProgress = "0";
        this.status = "Active";
    }

    public String getKraId() { return kraId; }
    public String getCompanyId() { return companyId; }
    public String getEmployeeId() { return employeeId; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTarget() { return target; }
    public String getDeadline() { return deadline; }

    public String getCurrentProgress() { return currentProgress; }
    public String getStatus() { return status; }

    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setCurrentProgress(String currentProgress) { this.currentProgress = currentProgress; }
    public void setStatus(String status) { this.status = status; }
}
