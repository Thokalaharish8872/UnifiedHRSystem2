package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
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
    private TextView tvDate, tvSummary, tvStatus;
    private TextInputEditText etReason;
    private TextInputLayout layoutReason;
    private MaterialButton btnSubmit;
    private Spinner spRequestType;
    private AttendanceService service;
    private Attendance request;
    private String employeeId, attendanceId, today, companyId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_attendance);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        employeeId = prefs.getString("employeeId", prefs.getString("userId", ""));
        companyId = prefs.getString("companyId", "");

        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        attendanceId = employeeId + "_" + today;

        service = new AttendanceService();

        initViews();
        loadData();
    }

    private void initViews() {
        tvDate = findViewById(R.id.tvDate);
        tvSummary = findViewById(R.id.tvRequestSummary);
        tvStatus = findViewById(R.id.tvInfo);

        etReason = findViewById(R.id.etReason);
        layoutReason = findViewById(R.id.layoutReason);
        btnSubmit = findViewById(R.id.btnSubmitRequest);
        spRequestType = findViewById(R.id.spRequestType);

        tvDate.setText(today);

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void loadData() {
        service.getAttendanceRequest(attendanceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                request = snapshot.getValue(Attendance.class);
                updateUi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void updateUi() {
        if (request == null) {
            tvSummary.setText("No request yet");
            tvStatus.setText("Pending");
            btnSubmit.setEnabled(true);
        } else {
            tvSummary.setText("Request: " + request.getRequestType());
            tvStatus.setText("Status: " + request.getStatus());
            etReason.setText(request.getReason());
            btnSubmit.setEnabled(false);
        }
    }

    private void submitRequest() {
        String reason = etReason.getText() != null ? etReason.getText().toString().trim() : "";
        String requestType = spRequestType.getSelectedItem().toString();

        if (TextUtils.isEmpty(reason)) {
            layoutReason.setError("Reason required");
            return;
        }
        layoutReason.setError(null);

        Attendance a = request != null ? request : new Attendance();

        a.setAttendanceId(attendanceId);
        a.setEmployeeId(employeeId);
        a.setDate(today);
        a.setReason(reason);
        a.setRequestType(requestType);
        a.setStatus(Attendance.STATUS_PENDING);
        a.setRequestedAt(System.currentTimeMillis());
        a.setCompanyId(companyId);

        btnSubmit.setEnabled(false);

        service.createAttendance(a, (err, ref) -> {
            if (err == null) {
                request = a;
                updateUi();
                Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            }
        });
    }
}
