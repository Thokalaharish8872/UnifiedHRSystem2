package com.unifiedhr.system.models;

public class Document {
    private String documentId;
    private String employeeId;
    private String documentType; // ID Proof, Certificate, Contract, etc.
    private String documentName;
    private String documentUrl;
    private String googleDriveId;
    private long uploadedAt;
    private String uploadedBy;

    public Document() {
    }

    public Document(String documentId, String employeeId, String documentType, String documentName) {
        this.documentId = documentId;
        this.employeeId = employeeId;
        this.documentType = documentType;
        this.documentName = documentName;
        this.uploadedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }

    public String getGoogleDriveId() { return googleDriveId; }
    public void setGoogleDriveId(String googleDriveId) { this.googleDriveId = googleDriveId; }

    public long getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(long uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
}








