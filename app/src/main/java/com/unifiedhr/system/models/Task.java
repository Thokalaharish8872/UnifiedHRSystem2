package com.unifiedhr.system.models;

public class Task {
    private String taskId;
    private String title;
    private String description;
    private String assignedTo;
    private String assignedBy;
    private String deadline;
    private String status; // Pending, In Progress, Completed
    private String priority; // Low, Medium, High
    private long createdAt;
    private long completedAt;
    private String notes;

    public Task() {
    }

    public Task(String taskId, String title, String description, String assignedTo, String assignedBy, String deadline) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.deadline = deadline;
        this.status = "Pending";
        this.priority = "Medium";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}








