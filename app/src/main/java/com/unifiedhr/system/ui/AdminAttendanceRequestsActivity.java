package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.AttendanceRequestAdapter;
import com.unifiedhr.system.models.Attendance;
import com.unifiedhr.system.services.AttendanceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAttendanceRequestsActivity extends AppCompatActivity implements AttendanceRequestAdapter.AttendanceActionListener {

    private AttendanceService attendanceService;
    private String adminId;

    private RecyclerView recyclerRequests;
    private TextView tvEmptyState;
    private AttendanceRequestAdapter adapter;
    private final List<Attendance> requests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        adminId = prefs.getString("userId", "");
        attendanceService = new AttendanceService();

        initViews();
        listenForRequests();
    }

    private void initViews() {
        recyclerRequests = findViewById(R.id.recyclerRequests);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceRequestAdapter(requests, AttendanceRequestAdapter.Mode.ADMIN, this);
        recyclerRequests.setAdapter(adapter);
    }

    private void listenForRequests() {
        attendanceService.getAllRequests()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requests.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Attendance attendance = child.getValue(Attendance.class);
                            if (attendance == null) {
                                continue;
                            }
                            String status = attendance.getStatus();
                            if (Attendance.STATUS_PENDING_MANAGER.equals(status)
                                    || Attendance.STATUS_PENDING_ADMIN.equals(status)) {
                                requests.add(attendance);
                            }
                        }
                        sortRequests();
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminAttendanceRequestsActivity.this,
                                R.string.toast_attendance_request_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sortRequests() {
        Collections.sort(requests, new Comparator<Attendance>() {
            @Override
            public int compare(Attendance o1, Attendance o2) {
                return Long.compare(o2.getRequestedAt(), o1.getRequestedAt());
            }
        });
    }

    private void updateEmptyState() {
        boolean hasItems = !requests.isEmpty();
        tvEmptyState.setVisibility(hasItems ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onApprove(Attendance attendance) {
        showCommentDialog(attendance, true);
    }

    @Override
    public void onReject(Attendance attendance) {
        showCommentDialog(attendance, false);
    }

    private void showCommentDialog(Attendance attendance, boolean approve) {
        View view = getLayoutInflater().inflate(R.layout.dialog_comment_input, null);
        TextInputEditText etComment = view.findViewById(R.id.etComment);

        new MaterialAlertDialogBuilder(this)
                .setTitle(approve ? R.string.action_approve : R.string.action_reject)
                .setView(view)
                .setPositiveButton(R.string.submit, (dialog, which) -> {
                    String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";
                    updateAttendanceStatus(attendance, approve, comment);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void updateAttendanceStatus(Attendance attendance, boolean approve, String comment) {
        if (TextUtils.isEmpty(adminId)) {
            Toast.makeText(this, R.string.toast_attendance_request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", approve ? Attendance.STATUS_ADMIN_APPROVED : Attendance.STATUS_ADMIN_REJECTED);
        updates.put("adminId", adminId);
        updates.put("adminComment", comment);
        updates.put("adminDecisionAt", System.currentTimeMillis());

        attendanceService.updateAttendance(attendance.getAttendanceId(), updates, (error, ref) -> {
            if (error == null) {
                Toast.makeText(this, R.string.toast_attendance_update_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_attendance_update_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}