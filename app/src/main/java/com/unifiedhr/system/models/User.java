package com.unifiedhr.system.models;

public class User {
    private String userId, email, name, role, companyId, employeeId, managerId, loginStatus;
    private boolean isRecruiter;
    private long createdAt;

    public User() {
    }

    public User(String userId, String email, String name, String role, String companyId) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.companyId = companyId;
        this.isRecruiter = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String getLoginStatus() { return loginStatus; }
    public void setLoginStatus(String loginStatus) { this.loginStatus = loginStatus; }
}



