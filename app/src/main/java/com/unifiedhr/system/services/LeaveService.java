package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.LeaveRequest;
import com.unifiedhr.system.utils.FirebaseHelper;

public class LeaveService {
    private DatabaseReference leaveRef;

    public LeaveService() {
        leaveRef = FirebaseHelper.getInstance().getDatabaseReference("leaveRequests");
    }

    public void createLeaveRequest(LeaveRequest leaveRequest, DatabaseReference.CompletionListener listener) {
        leaveRef.child(leaveRequest.getLeaveId()).setValue(leaveRequest, listener);
    }

    public void updateLeaveRequest(String leaveId, LeaveRequest leaveRequest, DatabaseReference.CompletionListener listener) {
        leaveRef.child(leaveId).setValue(leaveRequest, listener);
    }

    public DatabaseReference getLeaveRequest(String leaveId) {
        return leaveRef.child(leaveId);
    }

    public Query getLeaveRequestsByEmployee(String employeeId) {
        return leaveRef.orderByChild("employeeId").equalTo(employeeId);
    }

    public Query getPendingLeaveRequests() {
        return leaveRef.orderByChild("status").equalTo("Pending");
    }
}

