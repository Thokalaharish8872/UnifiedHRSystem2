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

    // Getters and Setters
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }

    public Double getOfficeLatitude() { return officeLatitude; }
    public void setOfficeLatitude(Double officeLatitude) { this.officeLatitude = officeLatitude; }

    public Double getOfficeLongitude() { return officeLongitude; }
    public void setOfficeLongitude(Double officeLongitude) { this.officeLongitude = officeLongitude; }

    public Integer getOfficeRadiusMeters() { return officeRadiusMeters; }
    public void setOfficeRadiusMeters(Integer officeRadiusMeters) { this.officeRadiusMeters = officeRadiusMeters; }

    public String getOfficeAddress() { return officeAddress; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }
}





