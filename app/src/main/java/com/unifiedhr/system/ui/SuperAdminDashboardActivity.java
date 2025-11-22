package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.AdminRequestAdapter;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.UserService;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminDashboardActivity extends AppCompatActivity implements AdminRequestAdapter.OnAdminRequestListener {

    private RecyclerView recyclerView;
    private AdminRequestAdapter adapter;
    private List<User> adminRequests;
    private UserService userService;
    private FirebaseAuth auth;
    private TextView tvWelcome, tvPendingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_dashboard);

        auth = FirebaseAuth.getInstance();

        tvWelcome = findViewById(R.id.tvWelcome);
        tvPendingRequests = findViewById(R.id.tvPendingRequests);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adminRequests = new ArrayList<>();
        adapter = new AdminRequestAdapter(adminRequests, this);
        recyclerView.setAdapter(adapter);

        userService = new UserService();
        loadAdminRequests();

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        String name = prefs.getString("userName", "Super Admin");
        tvWelcome.setText("Welcome, " + name);

        FloatingActionButton fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.super_admin_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void loadAdminRequests() {
        userService.getAllUsers().orderByChild("role").equalTo("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminRequests.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && "pending".equals(user.getLoginStatus())) {
                        adminRequests.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
                tvPendingRequests.setText(adminRequests.size() + " Pending Requests");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SuperAdminDashboardActivity.this, "Failed to load admin requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onApprove(User user) {
        user.setLoginStatus("approved");
        userService.updateUser(user.getUserId(), user, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Toast.makeText(this, "Admin approved.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to approve admin.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReject(User user) {
        user.setLoginStatus("rejected");
        userService.updateUser(user.getUserId(), user, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Toast.makeText(this, "Admin rejected.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to reject admin.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        auth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
