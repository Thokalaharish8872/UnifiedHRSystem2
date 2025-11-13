package com.unifiedhr.system.models;

public class Message {
    private String messageId;
    private String jobId;
    private String applicantId;
    private String recruiterId;
    private String senderId;
    private String senderName;
    private String message;
    private long timestamp;
    private boolean isRead;

    public Message() {
    }

    public Message(String messageId, String jobId, String applicantId, String recruiterId, 
                   String senderId, String senderName, String message) {
        this.messageId = messageId;
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.recruiterId = recruiterId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }

    public String getRecruiterId() { return recruiterId; }
    public void setRecruiterId(String recruiterId) { this.recruiterId = recruiterId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}






