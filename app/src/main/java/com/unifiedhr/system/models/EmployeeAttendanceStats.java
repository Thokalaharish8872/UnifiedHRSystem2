package com.unifiedhr.system.models;

public class EmployeeAttendanceStats {
    private String employeeId, employeeName;
    private int daysPresentThisWeek, daysPresentThisMonth, daysPresentThisYear;

    public EmployeeAttendanceStats(String employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public int getDaysPresentThisWeek() {
        return daysPresentThisWeek;
    }

    public void setDaysPresentThisWeek(int daysPresentThisWeek) {
        this.daysPresentThisWeek = daysPresentThisWeek;
    }

    public int getDaysPresentThisMonth() {
        return daysPresentThisMonth;
    }

    public void setDaysPresentThisMonth(int daysPresentThisMonth) {
        this.daysPresentThisMonth = daysPresentThisMonth;
    }

    public int getDaysPresentThisYear() {
        return daysPresentThisYear;
    }

    public void setDaysPresentThisYear(int daysPresentThisYear) {
        this.daysPresentThisYear = daysPresentThisYear;
    }
}
