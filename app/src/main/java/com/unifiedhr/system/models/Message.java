package com.unifiedhr.system.models;

public class Message {
    private String messageId, jobId, applicantId, recruiterId, senderId, senderName, message;
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

    public String getMessageId() { return messageId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getApplicantId() { return applicantId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }

}







