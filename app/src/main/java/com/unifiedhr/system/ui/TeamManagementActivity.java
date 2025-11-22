package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.TeamExpandableListAdapter;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.ui.fragments.AddTeamMemberDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamManagementActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private TeamExpandableListAdapter adapter;
    private List<User> managers;
    private HashMap<User, List<User>> managerEmployeeMap;
    private UserService userService;
    private FloatingActionButton btnAddMember;
    private String userId;
    private String userRole;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management_redesigned);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");
        companyId = prefs.getString("companyId", "");

        userService = new UserService();
        managers = new ArrayList<>();
        managerEmployeeMap = new HashMap<>();

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
        expandableListView = findViewById(R.id.expandableListView);
        adapter = new TeamExpandableListAdapter(this, managers, managerEmployeeMap);
        expandableListView.setAdapter(adapter);

        btnAddMember = findViewById(R.id.btnAddMember);
        btnAddMember.setOnClickListener(v -> showAddMemberDialog());
    }

    private void loadTeam() {
        userService.getAllUsers().orderByChild("companyId").equalTo(companyId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        managers.clear();
                        managerEmployeeMap.clear();

                        List<User> allUsers = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null) {
                                allUsers.add(user);
                            }
                        }

                        for (User user : allUsers) {
                            if ("Manager".equals(user.getRole())) {
                                managers.add(user);
                                managerEmployeeMap.put(user, new ArrayList<>());
                            }
                        }

                        for (User user : allUsers) {
                            if ("Employee".equals(user.getRole())) {
                                for (User manager : managers) {
                                    if (manager.getUserId().equals(user.getManagerId())) {
                                        managerEmployeeMap.get(manager).add(user);
                                        break;
                                    }
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
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
