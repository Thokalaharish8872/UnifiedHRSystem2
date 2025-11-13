package com.unifiedhr.system.models;

public class Expense {
    private String expenseId;
    private String employeeId;
    private String expenseType;
    private double amount;
    private String description;
    private String receiptUrl;
    private String status; // Pending, Approved, Rejected
    private String approvedBy;
    private String date;
    private long submittedAt;
    private long reviewedAt;

    public Expense() {
    }

    public Expense(String expenseId, String employeeId, String expenseType, double amount, String date) {
        this.expenseId = expenseId;
        this.employeeId = employeeId;
        this.expenseType = expenseType;
        this.amount = amount;
        this.date = date;
        this.status = "Pending";
        this.submittedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getExpenseType() { return expenseType; }
    public void setExpenseType(String expenseType) { this.expenseType = expenseType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(long submittedAt) { this.submittedAt = submittedAt; }

    public long getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(long reviewedAt) { this.reviewedAt = reviewedAt; }
}








