package com.unifiedhr.system.models;

public class Attendance {
    public static final String TYPE_PRESENT = "PRESENT";
    public static final String TYPE_LEAVE = "LEAVE";

    public static final String STATUS_PENDING_MANAGER = "PENDING_MANAGER";
    public static final String STATUS_PENDING_ADMIN = "PENDING_ADMIN";
    public static final String STATUS_MANAGER_APPROVED = "MANAGER_APPROVED";
    public static final String STATUS_MANAGER_REJECTED = "MANAGER_REJECTED";
    public static final String STATUS_ADMIN_APPROVED = "ADMIN_APPROVED";
    public static final String STATUS_ADMIN_REJECTED = "ADMIN_REJECTED";

    private String attendanceId;
    private String employeeId;
    private String date;
    private String requestType;
    private String reason;
    private String status;
    private String managerId;
    private String adminId;
    private String managerComment;
    private String adminComment;
    private long requestedAt;
    private long managerDecisionAt;
    private long adminDecisionAt;

    public Attendance() {
    }

    public Attendance(String attendanceId, String employeeId, String date) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.date = date;
        this.requestedAt = System.currentTimeMillis();
        this.status = STATUS_PENDING_MANAGER;
        this.requestType = TYPE_PRESENT;
    }

    public String getAttendanceId() { return attendanceId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public void setManagerComment(String managerComment) { this.managerComment = managerComment; }
    public String getManagerComment() { return managerComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }
    public String getAdminComment() { return adminComment; }
    public long getRequestedAt() { return requestedAt; }
    public void setRequestedAt(long requestedAt) { this.requestedAt = requestedAt; }
    public void setManagerDecisionAt(long managerDecisionAt) { this.managerDecisionAt = managerDecisionAt; }
    public void setAdminDecisionAt(long adminDecisionAt) { this.adminDecisionAt = adminDecisionAt; }
    public long getManagerDecisionAt() { return managerDecisionAt; }
    public long getAdminDecisionAt() { return adminDecisionAt; }
}



