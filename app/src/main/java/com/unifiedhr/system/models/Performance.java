package com.unifiedhr.system.models;

public class Performance {
    private String performanceId;
    private String employeeId;
    private String month;
    private String year;
    private double attendancePercentage;
    private double taskCompletionPercentage;
    private double rating; // 1-5
    private String managerNotes;
    private long createdAt;

    public Performance() {
    }

    public Performance(String performanceId, String employeeId, String month, String year) {
        this.performanceId = performanceId;
        this.employeeId = employeeId;
        this.month = month;
        this.year = year;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getPerformanceId() { return performanceId; }
    public void setPerformanceId(String performanceId) { this.performanceId = performanceId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public double getTaskCompletionPercentage() { return taskCompletionPercentage; }
    public void setTaskCompletionPercentage(double taskCompletionPercentage) { this.taskCompletionPercentage = taskCompletionPercentage; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getManagerNotes() { return managerNotes; }
    public void setManagerNotes(String managerNotes) { this.managerNotes = managerNotes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}








