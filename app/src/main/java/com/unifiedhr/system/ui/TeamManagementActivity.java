package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.TeamAdapter;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.ui.fragments.AddTeamMemberDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class TeamManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TeamAdapter adapter;
    private List<User> teamList;
    private UserService userService;
    private Button btnAddMember;
    private String userId;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");

        userService = new UserService();
        teamList = new ArrayList<>();

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        initViews();
        loadTeam();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeamAdapter(teamList);
        recyclerView.setAdapter(adapter);

        btnAddMember = findViewById(R.id.btnAddMember);
        btnAddMember.setOnClickListener(v -> showAddMemberDialog());
    }

    private void loadTeam() {
        if (userRole.equals("Admin")) {
            // Load all employees
            String companyId = getSharedPreferences("UnifiedHR", MODE_PRIVATE).getString("companyId", "");
            userService.getAllUsers().orderByChild("companyId").equalTo(companyId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        teamList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null && !user.getRole().equals("Admin")) {
                                teamList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
        } else if (userRole.equals("Manager")) {
            // Load team members
            userService.getAllUsers().orderByChild("managerId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        teamList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null) {
                                teamList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
        }
    }

    private void showAddMemberDialog() {
        AddTeamMemberDialogFragment dialog = new AddTeamMemberDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddMember");
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

