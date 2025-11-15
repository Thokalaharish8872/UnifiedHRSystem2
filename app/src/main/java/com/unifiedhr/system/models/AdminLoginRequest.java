package com.unifiedhr.system.models;

public class AdminLoginRequest {
    private String requestId;
    private String userId;
    private String email;
    private String name;
    private String status; // pending, approved, rejected
    private long requestedAt;
    private long reviewedAt;
    private String reviewedBy; // SuperAdmin userId who reviewed

    public AdminLoginRequest() {
    }

    public AdminLoginRequest(String requestId, String userId, String email, String name) {
        this.requestId = requestId;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.status = "pending";
        this.requestedAt = System.currentTimeMillis();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public long getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(long reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}