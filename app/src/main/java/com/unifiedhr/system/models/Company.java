package com.unifiedhr.system.models;

public class Company {
    private String companyId, name, adminId;
    private int employeeCount, officeRadiusMeters;

    public Company() {
    }

    public Company(String companyId, String name, String adminId) {
        this.companyId = companyId;
        this.name = name;
        this.employeeCount = 0;
        this.adminId = adminId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setOfficeRadiusMeters(int officeRadiusMeters) {
        this.officeRadiusMeters = officeRadiusMeters;
    }
}
