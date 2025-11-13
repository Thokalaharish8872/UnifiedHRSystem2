package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.DailyReport;
import com.unifiedhr.system.utils.FirebaseHelper;

public class DailyReportService {
    private DatabaseReference reportRef;

    public DailyReportService() {
        reportRef = FirebaseHelper.getInstance().getDatabaseReference("dailyReports");
    }

    public void createDailyReport(DailyReport report, DatabaseReference.CompletionListener listener) {
        reportRef.child(report.getReportId()).setValue(report, listener);
    }
}

