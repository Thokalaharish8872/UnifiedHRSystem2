package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.Message;
import com.unifiedhr.system.utils.FirebaseHelper;

public class MessageService {
    private DatabaseReference messagesRef;

    public MessageService() {
        messagesRef = FirebaseHelper.getInstance().getDatabaseReference("messages");
    }

    public void sendMessage(Message message, DatabaseReference.CompletionListener listener) {
        messagesRef.child(message.getMessageId()).setValue(message, listener);
    }

    public Query getMessagesByJobAndApplicant(String jobId, String applicantId) {
        return messagesRef.orderByChild("jobId").equalTo(jobId);
    }

    public Query getMessagesByApplicant(String applicantId) {
        return messagesRef.orderByChild("applicantId").equalTo(applicantId);
    }

    public void markAsRead(String messageId, DatabaseReference.CompletionListener listener) {
        messagesRef.child(messageId).child("isRead").setValue(true, listener);
    }
}






