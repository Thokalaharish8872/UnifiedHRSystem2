package com.unifiedhr.system.models;

public class Attendance {

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_APPROVED_MANAGER = "approved_by_manager";
    public static final String STATUS_APPROVED_ADMIN = "approved_by_admin";

    public static final String STATUS_REJECTED_MANAGER = "rejected_by_manager";
    public static final String STATUS_REJECTED_ADMIN = "rejected_by_admin";

    private String attendanceId, employeeId, date, status, requestType, reason,adminId, companyId, managerId;
    private long requestedAt, managerDecisionAt, adminDecisionAt;

    public Attendance() {}

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

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
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
    public long getAdminDecisionAt() {
        return adminDecisionAt;
    }

    public void setAdminDecisionAt(long adminDecisionAt) {
        this.adminDecisionAt = adminDecisionAt;
    }
}
