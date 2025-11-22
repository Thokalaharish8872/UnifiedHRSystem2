package com.unifiedhr.system.models;

public class Task {

    private String taskId, title, description, assignedTo, assignedBy, deadline, status, notes, parentTaskId, level, companyId;
    private long createdAt, completedAt;

    public Task() {}

    public Task(String taskId, String title, String description,
                String assignedTo, String assignedBy,
                String deadline, String parentTaskId, String level, String companyId) {

        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.deadline = deadline;
        this.status = "Pending";
        this.createdAt = System.currentTimeMillis();

        this.parentTaskId = parentTaskId;
        this.level = level;
        this.companyId = companyId;
    }


    public String getTaskId() { return taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAssignedTo() { return assignedTo; }

    public String getAssignedBy() { return assignedBy; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getParentTaskId() { return parentTaskId; }


    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
}
