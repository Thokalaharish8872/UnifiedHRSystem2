package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.Attendance;
import com.unifiedhr.system.utils.FirebaseHelper;

import java.util.Map;

public class AttendanceService {
    private DatabaseReference attendanceRef;

    public AttendanceService() {
        attendanceRef = FirebaseHelper.getInstance().getDatabaseReference("attendance");
    }

    public Query getAttendanceByEmployeeAndDate(String employeeId, String date) {
        return attendanceRef.orderByChild("employeeId").equalTo(employeeId);
    }

    public void createAttendance(Attendance attendance, DatabaseReference.CompletionListener listener) {
        attendanceRef.child(attendance.getAttendanceId()).setValue(attendance, listener);
    }

    public void updateAttendance(String attendanceId, Map<String, Object> updates, DatabaseReference.CompletionListener listener) {
        attendanceRef.child(attendanceId).updateChildren(updates, listener);
    }
}

