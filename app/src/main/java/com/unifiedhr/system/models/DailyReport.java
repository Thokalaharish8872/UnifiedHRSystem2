package com.unifiedhr.system.models;

public class DailyReport {
    private String reportId;
    private String employeeId;
    private String date;
    private String workDone;
    private String challenges;
    private String nextDayPlan;
    private long timestamp;

    public DailyReport() {
    }

    public DailyReport(String reportId, String employeeId, String date) {
        this.reportId = reportId;
        this.employeeId = employeeId;
        this.date = date;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getWorkDone() { return workDone; }
    public void setWorkDone(String workDone) { this.workDone = workDone; }

    public String getChallenges() { return challenges; }
    public void setChallenges(String challenges) { this.challenges = challenges; }

    public String getNextDayPlan() { return nextDayPlan; }
    public void setNextDayPlan(String nextDayPlan) { this.nextDayPlan = nextDayPlan; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}








