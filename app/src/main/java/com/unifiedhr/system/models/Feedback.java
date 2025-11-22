package com.unifiedhr.system.models;

public class Feedback {
    private String message, sentBy;
    private long timestamp;

    public Feedback() {}

    public String getMessage() { return message; }
    public String getSentBy() { return sentBy; }
    public long getTimestamp() { return timestamp; }
}
