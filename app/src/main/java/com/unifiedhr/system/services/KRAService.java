package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.KRA;

import java.util.HashMap;
import java.util.Map;

public class KRAService {

    private final DatabaseReference kraRef;

    public KRAService(String companyId) {
        this.kraRef = FirebaseDatabase.getInstance()
                .getReference("company_kras")
                .child(companyId);
    }

    public Query getAllKrasByCompany(String companyId) {
        return FirebaseDatabase.getInstance()
                .getReference("company_kras")
                .child(companyId);
    }

    public Query getKrasByEmployee(String employeeId) {
        return kraRef.orderByChild("employeeId").equalTo(employeeId);
    }

    public Query getKrasAssignedBy(String managerId) {
        return kraRef.orderByChild("assignedBy").equalTo(managerId);
    }

    public void createKRA(KRA kra, DatabaseReference.CompletionListener listener) {
        kraRef.child(kra.getKraId()).setValue(kra, listener);
    }

    public void deleteKRA(String kraId, DatabaseReference.CompletionListener listener) {
        kraRef.child(kraId).removeValue(listener);
    }

    public void updateProgress(String kraId, String newProgress,
                               DatabaseReference.CompletionListener listener) {
        Map<String, Object> map = new HashMap<>();
        map.put("currentProgress", newProgress);
        kraRef.child(kraId).updateChildren(map, listener);
    }
}
