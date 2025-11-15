package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.AdminLoginRequestAdapter;
import com.unifiedhr.system.models.AdminLoginRequest;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.AdminLoginRequestService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AdminLoginRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminLoginRequestAdapter adapter;
    private AdminLoginRequestService requestService;
    private UserService userService;
    private List<AdminLoginRequest> requestList;
    private SharedPreferences prefs;
    private String companyId;  // SuperAdmin company

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login_requests);

        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        companyId = prefs.getString("companyId", null); // super admin company

        if (companyId == null) {
            Toast.makeText(this, "Company ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        requestService = new AdminLoginRequestService();
        userService = new UserService();
        requestList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Admin Login Requests");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminLoginRequestAdapter(requestList, new AdminLoginRequestAdapter.RequestActionListener() {
            @Override
            public void onApprove(AdminLoginRequest request) {
                approveRequest(request);
            }

            @Override
            public void onReject(AdminLoginRequest request) {
                rejectRequest(request);
            }
        });

        recyclerView.setAdapter(adapter);

        loadPendingRequests();
    }

    private void loadPendingRequests() {

        requestService.getPendingRequests(companyId)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        requestList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            AdminLoginRequest request = data.getValue(AdminLoginRequest.class);
                            if (request != null) {
                                requestList.add(request);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (requestList.isEmpty()) {
                            Toast.makeText(AdminLoginRequestsActivity.this,
                                    "No pending requests",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(AdminLoginRequestsActivity.this,
                                "Error loading requests: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void approveRequest(AdminLoginRequest request) {

        String superAdminId = FirebaseHelper.getInstance().getCurrentUserId();

        requestService.updateRequestStatus(companyId, request.getRequestId(),
                "approved", superAdminId, (error, ref) -> {

                    if (error == null) {

                        // update admin loginStatus
                        userService.getUser(request.getUserId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);

                                        if (user != null) {
                                            user.setLoginStatus("approved");

                                            userService.createUser(user, (e, r) -> {

                                                if (e == null) {
                                                    Toast.makeText(
                                                            AdminLoginRequestsActivity.this,
                                                            "Approved",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(
                                                            AdminLoginRequestsActivity.this,
                                                            "Failed to update user",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(
                                                AdminLoginRequestsActivity.this,
                                                "Error: " + error.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Failed to approve", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void rejectRequest(AdminLoginRequest request) {

        String superAdminId = FirebaseHelper.getInstance().getCurrentUserId();

        requestService.updateRequestStatus(companyId, request.getRequestId(),
                "rejected", superAdminId, (error, ref) -> {

                    if (error == null) {

                        userService.getUser(request.getUserId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {

                                        User user = snapshot.getValue(User.class);

                                        if (user != null) {
                                            user.setLoginStatus("rejected");

                                            userService.createUser(user, (e, r) -> {

                                                if (e == null) {
                                                    Toast.makeText(
                                                            AdminLoginRequestsActivity.this,
                                                            "Rejected",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(
                                                            AdminLoginRequestsActivity.this,
                                                            "Failed to update user",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(
                                                AdminLoginRequestsActivity.this,
                                                "Error: " + error.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Failed to reject", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
