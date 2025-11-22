package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.AttendanceRequestAdapter;
import com.unifiedhr.system.models.Attendance;
import com.unifiedhr.system.services.AttendanceService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerAttendanceRequestsActivity extends AppCompatActivity implements AttendanceRequestAdapter.AttendanceActionListener {

    private AttendanceService service;
    private String managerId;
    private String companyId;
    private UserService userService;

    private RecyclerView recyclerView;
    private AttendanceRequestAdapter adapter;
    private final List<Attendance> list = new ArrayList<>();
    private final List<User> employees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_attendance_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        managerId = prefs.getString("userId", "");
        companyId = prefs.getString("companyId", "");

        service = new AttendanceService();
        userService = new UserService();

        recyclerView = findViewById(R.id.recyclerRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AttendanceRequestAdapter(list, AttendanceRequestAdapter.Mode.MANAGER, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabSubmitOwnAttendance);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AttendanceActivity.class)));

        loadEmployees();
    }

    private void loadEmployees() {
        userService.getAllUsers().orderByChild("managerId").equalTo(managerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employees.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    employees.add(user);
                }
                loadRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadRequests() {
        service.getAllRequests().orderByChild("companyId").equalTo(companyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Attendance a = snap.getValue(Attendance.class);
                    if (a != null && Attendance.STATUS_PENDING.equals(a.getStatus())) {
                        for (User employee : employees) {
                            if (employee.getUserId().equals(a.getEmployeeId())) {
                                list.add(a);
                                break;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onApprove(Attendance a) {
        a.setStatus(Attendance.STATUS_APPROVED_MANAGER);
        a.setManagerId(managerId);
        a.setManagerDecisionAt(System.currentTimeMillis());

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", a.getStatus());
        updates.put("managerId", a.getManagerId());
        updates.put("managerDecisionAt", a.getManagerDecisionAt());

        service.updateAttendance(a.getAttendanceId(), updates, (err, ref) ->
                Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onReject(Attendance a) {
        a.setStatus(Attendance.STATUS_REJECTED_MANAGER);
        a.setManagerId(managerId);
        a.setManagerDecisionAt(System.currentTimeMillis());

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", a.getStatus());
        updates.put("managerId", a.getManagerId());
        updates.put("managerDecisionAt", a.getManagerDecisionAt());

        service.updateAttendance(a.getAttendanceId(), updates, (err, ref) ->
                Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
