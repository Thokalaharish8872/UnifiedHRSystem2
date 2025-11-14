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

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public void setDate(String date) { this.date = date; }
    public void setWorkDone(String workDone) { this.workDone = workDone; }
    public void setChallenges(String challenges) { this.challenges = challenges; }
    public void setNextDayPlan(String nextDayPlan) { this.nextDayPlan = nextDayPlan; }
}








