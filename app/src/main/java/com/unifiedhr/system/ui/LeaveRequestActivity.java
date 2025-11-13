package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseError;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.LeaveRequest;
import com.unifiedhr.system.services.LeaveService;
import com.unifiedhr.system.utils.Utils;

public class LeaveRequestActivity extends AppCompatActivity {
    private EditText etStartDate, etEndDate, etReason;
    private Spinner spinnerLeaveType;
    private Button btnSubmit;
    private LeaveService leaveService;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        employeeId = prefs.getString("employeeId", "");

        leaveService = new LeaveService();

        initViews();
    }

    private void initViews() {
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etReason = findViewById(R.id.etReason);
        spinnerLeaveType = findViewById(R.id.spinnerLeaveType);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitLeaveRequest());
    }

    private void submitLeaveRequest() {
        String leaveType = spinnerLeaveType.getSelectedItem().toString();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        if (startDate.isEmpty() || endDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveId(Utils.generateId());
        leaveRequest.setEmployeeId(employeeId);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setReason(reason);

        leaveService.createLeaveRequest(leaveRequest, (error, ref) -> {
            if (error == null) {
                Toast.makeText(this, "Leave request submitted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to submit leave request", Toast.LENGTH_SHORT).show();
            }
        });
    }
}








