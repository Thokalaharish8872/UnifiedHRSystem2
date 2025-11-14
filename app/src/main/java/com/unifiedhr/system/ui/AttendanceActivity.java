package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Attendance;
import com.unifiedhr.system.services.AttendanceService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {

    private TextView tvDate;
    private TextView tvRequestSummary;
    private TextView tvManagerStatus;
    private TextView tvAdminStatus;
    private TextView tvInfo;
    private Spinner spRequestType;
    private TextInputLayout layoutReason;
    private TextInputEditText etReason;
    private MaterialButton btnSubmitRequest;
    private View cardRequestForm;

    private AttendanceService attendanceService;
    private Attendance currentRequest;

    private String userId;
    private String employeeId;
    private String managerId;
    private String userRole;
    private String todayDate;
    private String attendanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        employeeId = prefs.getString("employeeId", "");
        userRole = prefs.getString("userRole", "");
        managerId = prefs.getString("managerId", "");
        if (TextUtils.isEmpty(employeeId)) {
            employeeId = userId;
        }

        attendanceService = new AttendanceService();
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        attendanceId = employeeId + "_" + todayDate;

        initViews();
        loadExistingRequest();
    }

    private void initViews() {
        tvDate = findViewById(R.id.tvDate);
        tvRequestSummary = findViewById(R.id.tvRequestSummary);
        tvManagerStatus = findViewById(R.id.tvManagerStatus);
        tvAdminStatus = findViewById(R.id.tvAdminStatus);
        tvInfo = findViewById(R.id.tvInfo);
        spRequestType = findViewById(R.id.spRequestType);
        layoutReason = findViewById(R.id.layoutReason);
        etReason = findViewById(R.id.etReason);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        cardRequestForm = findViewById(R.id.cardRequestForm);

        tvDate.setText(todayDate);

        spRequestType.setSelection(0);
        spRequestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                layoutReason.setHint(position == 0
                        ? getString(R.string.hint_attendance_reason)
                        : getString(R.string.reason));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSubmitRequest.setOnClickListener(v -> submitAttendanceRequest());
    }

    private void loadExistingRequest() {
        if (TextUtils.isEmpty(employeeId)) {
            Toast.makeText(this, R.string.toast_attendance_request_failed, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        attendanceService.getAttendanceRequest(attendanceId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentRequest = snapshot.getValue(Attendance.class);
                        updateUiState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AttendanceActivity.this,
                                R.string.toast_attendance_request_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitAttendanceRequest() {
        String selectedType = spRequestType.getSelectedItemPosition() == 0
                ? Attendance.TYPE_PRESENT
                : Attendance.TYPE_LEAVE;

        String reason = etReason.getText() != null ? etReason.getText().toString().trim() : "";

        if (TextUtils.isEmpty(reason)) {
            layoutReason.setError(getString(R.string.toast_attendance_reason_required));
            return;
        } else {
            layoutReason.setError(null);
        }

        boolean isAdminUser = "Admin".equalsIgnoreCase(userRole);
        boolean isManagerUser = "Manager".equalsIgnoreCase(userRole);

        if (currentRequest != null
                && (Attendance.STATUS_ADMIN_APPROVED.equals(currentRequest.getStatus())
                || Attendance.STATUS_ADMIN_REJECTED.equals(currentRequest.getStatus()))) {
            Toast.makeText(this, R.string.toast_attendance_already_finalized, Toast.LENGTH_LONG).show();
            return;
        }

        boolean isReSubmit = currentRequest != null
                && (Attendance.STATUS_MANAGER_REJECTED.equals(currentRequest.getStatus())
                || Attendance.STATUS_ADMIN_REJECTED.equals(currentRequest.getStatus()));

        Attendance request = currentRequest != null ? currentRequest : new Attendance(attendanceId, employeeId, todayDate);
        request.setEmployeeId(employeeId);
        request.setDate(todayDate);
        request.setRequestType(selectedType);
        request.setReason(reason);
        request.setRequestedAt(System.currentTimeMillis());

        if (isAdminUser) {
            request.setManagerId(null);
            request.setManagerComment(null);
            request.setManagerDecisionAt(0);
            request.setStatus(Attendance.STATUS_ADMIN_APPROVED);
            request.setAdminId(userId);
            request.setAdminComment(getString(R.string.auto_admin_self_comment));
            request.setAdminDecisionAt(System.currentTimeMillis());
        } else if (isManagerUser) {
            request.setManagerId(userId);
            request.setManagerComment(null);
            request.setManagerDecisionAt(0);
            request.setStatus(Attendance.STATUS_PENDING_ADMIN);
            request.setAdminId(null);
            request.setAdminComment(null);
            request.setAdminDecisionAt(0);
        } else {
            if (TextUtils.isEmpty(managerId)) {
                request.setManagerId(null);
                request.setStatus(Attendance.STATUS_PENDING_ADMIN);
            } else {
                request.setManagerId(managerId);
                request.setStatus(Attendance.STATUS_PENDING_MANAGER);
            }
            request.setManagerComment(null);
            request.setManagerDecisionAt(0);
            request.setAdminId(null);
            request.setAdminComment(null);
            request.setAdminDecisionAt(0);
        }

        attendanceService.createAttendance(request, (error, ref) -> {
            if (error == null) {
                currentRequest = request;
                updateUiState();
                Toast.makeText(this,
                        isReSubmit ? R.string.toast_attendance_request_updated
                                : R.string.toast_attendance_request_submitted,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_attendance_request_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUiState() {
        if (currentRequest == null) {
            tvRequestSummary.setText(R.string.status_no_request);
            tvManagerStatus.setText(R.string.status_manager_pending);
            tvAdminStatus.setText(R.string.status_admin_pending);
            setFormEnabled(true);
            tvInfo.setText(R.string.info_attendance_request);
            return;
        }

        String typeLabel = Attendance.TYPE_LEAVE.equals(currentRequest.getRequestType())
                ? getString(R.string.attendance_type_leave)
                : getString(R.string.attendance_type_present);
        String summary = typeLabel + " - " + todayDate;
        tvRequestSummary.setText(summary);

        tvManagerStatus.setText(getManagerStatusLabel(currentRequest));
        tvAdminStatus.setText(getAdminStatusLabel(currentRequest));

        boolean allowResubmit = Attendance.STATUS_MANAGER_REJECTED.equals(currentRequest.getStatus())
                || Attendance.STATUS_ADMIN_REJECTED.equals(currentRequest.getStatus());

        boolean canEdit = allowResubmit || currentRequest == null;
        setFormEnabled(canEdit);

        String status = currentRequest.getStatus();
        if (allowResubmit) {
            tvInfo.setText(R.string.toast_attendance_resubmit_after_reject);
        } else if (Attendance.STATUS_PENDING_MANAGER.equals(status)) {
            tvInfo.setText(R.string.status_manager_pending);
        } else if (Attendance.STATUS_PENDING_ADMIN.equals(status)) {
            tvInfo.setText(R.string.status_admin_pending);
        } else if (Attendance.STATUS_MANAGER_APPROVED.equals(status)
                || Attendance.STATUS_ADMIN_APPROVED.equals(status)) {
            tvInfo.setText(R.string.attendance_marked);
        } else {
            tvInfo.setText(R.string.info_attendance_request);
        }

        etReason.setText(currentRequest.getReason());
        spRequestType.setSelection(Attendance.TYPE_LEAVE.equals(currentRequest.getRequestType()) ? 1 : 0);
    }

    private void setFormEnabled(boolean enabled) {
        cardRequestForm.setVisibility(View.VISIBLE);
        spRequestType.setEnabled(enabled);
        layoutReason.setEnabled(enabled);
        etReason.setEnabled(enabled);
        btnSubmitRequest.setEnabled(enabled);
    }

    private String getManagerStatusLabel(Attendance request) {
        String status = request.getStatus();
        switch (status) {
            case Attendance.STATUS_MANAGER_APPROVED:
                return getString(R.string.status_manager_approved);
            case Attendance.STATUS_MANAGER_REJECTED:
                return getString(R.string.status_manager_rejected);
            case Attendance.STATUS_PENDING_MANAGER:
                return getString(R.string.status_manager_pending);
            case Attendance.STATUS_PENDING_ADMIN:
                if (TextUtils.isEmpty(request.getManagerId())
                        || TextUtils.equals(request.getManagerId(), request.getEmployeeId())) {
                    return getString(R.string.status_manager_not_required);
                }
                return getString(R.string.status_manager_pending);
            case Attendance.STATUS_ADMIN_APPROVED:
            case Attendance.STATUS_ADMIN_REJECTED:
                if (request.getManagerDecisionAt() > 0) {
                    return getString(R.string.status_manager_approved);
                }
                return getString(R.string.status_manager_not_required);
            default:
                return getString(R.string.status_manager_pending);
        }
    }

    private String getAdminStatusLabel(Attendance request) {
        String status = request.getStatus();
        switch (status) {
            case Attendance.STATUS_ADMIN_APPROVED:
                return getString(R.string.status_admin_approved);
            case Attendance.STATUS_ADMIN_REJECTED:
                return getString(R.string.status_admin_rejected);
            case Attendance.STATUS_MANAGER_APPROVED:
            case Attendance.STATUS_MANAGER_REJECTED:
                return getString(R.string.status_admin_not_required);
            case Attendance.STATUS_PENDING_MANAGER:
            case Attendance.STATUS_PENDING_ADMIN:
                return getString(R.string.status_admin_pending);
            default:
                return getString(R.string.status_admin_pending);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
