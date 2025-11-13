package com.unifiedhr.system.models;

public class Attendance {
    private String attendanceId;
    private String employeeId;
    private String date;
    private String checkInTime;
    private String checkOutTime;
    private String checkInLocation;
    private String checkOutLocation;
    private String attendanceType; // GPS, QR, Web, WFH
    private boolean isPresent;
    private long timestamp;

    public Attendance() {
    }

    public Attendance(String attendanceId, String employeeId, String date) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.date = date;
        this.timestamp = System.currentTimeMillis();
        this.isPresent = false;
    }

    // Getters and Setters
    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getCheckInLocation() { return checkInLocation; }
    public void setCheckInLocation(String checkInLocation) { this.checkInLocation = checkInLocation; }

    public String getCheckOutLocation() { return checkOutLocation; }
    public void setCheckOutLocation(String checkOutLocation) { this.checkOutLocation = checkOutLocation; }

    public String getAttendanceType() { return attendanceType; }
    public void setAttendanceType(String attendanceType) { this.attendanceType = attendanceType; }

    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}



