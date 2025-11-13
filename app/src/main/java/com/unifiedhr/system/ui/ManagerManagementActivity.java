package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.ManagerAdapter;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.ui.fragments.AddManagerDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ManagerManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ManagerAdapter adapter;
    private List<User> managerList;
    private UserService userService;
    private Button btnAddManager;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_management);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        companyId = prefs.getString("companyId", "");

        userService = new UserService();
        managerList = new ArrayList<>();

        initViews();
        loadManagers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManagerAdapter(managerList);
        recyclerView.setAdapter(adapter);

        btnAddManager = findViewById(R.id.btnAddManager);
        btnAddManager.setOnClickListener(v -> showAddManagerDialog());
    }

    private void loadManagers() {
        userService.getAllUsers().orderByChild("companyId").equalTo(companyId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    managerList.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null && user.getRole().equals("Manager")) {
                            managerList.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
    }

    private void showAddManagerDialog() {
        AddManagerDialogFragment dialog = new AddManagerDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddManager");
    }
}

