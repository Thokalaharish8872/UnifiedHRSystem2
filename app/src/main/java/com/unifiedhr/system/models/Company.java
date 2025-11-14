package com.unifiedhr.system.models;

public class Company {
    private String companyId;
    private String companyName;
    private String adminId;
    private String address;
    private String phone;
    private String email;
    private long createdAt;
    private int employeeCount;
    private Double officeLatitude;
    private Double officeLongitude;
    private Integer officeRadiusMeters;
    private String officeAddress;

    public Company() {
    }

    public Company(String companyId, String companyName, String adminId) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.adminId = adminId;
        this.createdAt = System.currentTimeMillis();
        this.employeeCount = 0;
        this.officeRadiusMeters = 200;
    }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setOfficeRadiusMeters(Integer officeRadiusMeters) { this.officeRadiusMeters = officeRadiusMeters; }
}





