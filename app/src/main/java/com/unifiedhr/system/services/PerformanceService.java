package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.Performance;
import com.unifiedhr.system.utils.FirebaseHelper;

public class PerformanceService {
    private DatabaseReference performanceRef;

    public PerformanceService() {
        performanceRef = FirebaseHelper.getInstance().getDatabaseReference("performance");
    }

    public void createPerformance(Performance performance, DatabaseReference.CompletionListener listener) {
        performanceRef.child(performance.getPerformanceId()).setValue(performance, listener);
    }

    public DatabaseReference getPerformance(String performanceId) {
        return performanceRef.child(performanceId);
    }

    public Query getPerformanceByEmployee(String employeeId) {
        return performanceRef.orderByChild("employeeId").equalTo(employeeId);
    }

    public Query getPerformanceByMonth(String month, String year) {
        return performanceRef.orderByChild("month").equalTo(month);
    }
}

