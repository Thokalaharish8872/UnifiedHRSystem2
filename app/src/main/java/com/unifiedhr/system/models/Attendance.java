package com.unifiedhr.system.models;

public class Attendance {
    public static final String TYPE_LEAVE = "leave";
    public static final String TYPE_PRESENT = "present";

    public static final String STATUS_PENDING_MANAGER = "pending_manager";
    public static final String STATUS_PENDING_ADMIN = "pending_admin";
    public static final String STATUS_MANAGER_APPROVED = "manager_approved";
    public static final String STATUS_MANAGER_REJECTED = "manager_rejected";
    public static final String STATUS_ADMIN_APPROVED = "admin_approved";
    public static final String STATUS_ADMIN_REJECTED = "admin_rejected";

    private String attendanceId;
    private String employeeId;
    private String date;
    private String status;
    private String requestType;
    private String reason;
    private long requestedAt;
    private String managerId;
    private String managerComment;
    private long managerDecisionAt;
    private String adminId;
    private String adminComment;
    private long adminDecisionAt;

    public Attendance() {
        // Default constructor required for calls to DataSnapshot.getValue(Attendance.class)
    }

    public Attendance(String employeeId, String date, String status) {
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }

    public long getManagerDecisionAt() {
        return managerDecisionAt;
    }

    public void setManagerDecisionAt(long managerDecisionAt) {
        this.managerDecisionAt = managerDecisionAt;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public long getAdminDecisionAt() {
        return adminDecisionAt;
    }

    public void setAdminDecisionAt(long adminDecisionAt) {
        this.adminDecisionAt = adminDecisionAt;
    }
}
