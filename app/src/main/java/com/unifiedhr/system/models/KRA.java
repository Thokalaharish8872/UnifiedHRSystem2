package com.unifiedhr.system.models;

public class KRA {
    private String kraId;
    private String employeeId;
    private String title;
    private String description;
    private String target;
    private String currentProgress;
    private String deadline;
    private String status;

    public KRA() {
    }

    public KRA(String kraId, String employeeId, String title, String description) {
        this.kraId = kraId;
        this.employeeId = employeeId;
        this.title = title;
        this.description = description;
        this.status = "Active";
    }

    // Getters and Setters
    public String getKraId() { return kraId; }
    public void setKraId(String kraId) { this.kraId = kraId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(String currentProgress) { this.currentProgress = currentProgress; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}








