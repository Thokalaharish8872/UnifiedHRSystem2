package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.AdminLoginRequest;
import com.unifiedhr.system.utils.FirebaseHelper;

public class AdminLoginRequestService {

    public DatabaseReference getCompanyRequestsRef(String companyId) {
        return FirebaseHelper.getInstance()
                .getDatabaseReference("companies")
                .child(companyId)
                .child("adminRequests");
    }
    public Query getPendingRequests(String companyId) {
        return getCompanyRequestsRef(companyId)
                .orderByChild("status")
                .equalTo("pending");
    }
    public void updateRequestStatus(String companyId, String requestId, String status, String reviewedBy, DatabaseReference.CompletionListener listener) {
        DatabaseReference ref = getCompanyRequestsRef(companyId).child(requestId);
        ref.child("status").setValue(status, listener);
        ref.child("reviewedAt").setValue(System.currentTimeMillis());
        ref.child("reviewedBy").setValue(reviewedBy);
    }
}
