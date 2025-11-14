package com.unifiedhr.system.models;

public class User {
    private String userId;
    private String email;
    private String name;
    private String role; // SuperAdmin, Admin, Manager, Employee, JobSeeker
    private String companyId;
    private String employeeId;
    private String managerId;
    private String department;
    private String phone;
    private boolean isRecruiter;
    private String loginStatus; // For Admin: pending, approved, rejected. For others: null or approved
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
    
    public boolean isRecruiter() { return isRecruiter; }
    public void setRecruiter(boolean recruiter) { this.isRecruiter = recruiter; }
    
    public String getLoginStatus() { return loginStatus; }
    public void setLoginStatus(String loginStatus) { this.loginStatus = loginStatus; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public void setRole(String role) { this.role = role; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}



