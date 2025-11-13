package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.TaskAdapter;
import com.unifiedhr.system.models.Task;
import com.unifiedhr.system.services.TaskService;
import com.unifiedhr.system.ui.fragments.CreateTaskDialogFragment;
import com.unifiedhr.system.utils.FirebaseHelper;
import com.unifiedhr.system.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private TaskService taskService;
    private Button btnCreateTask;
    private String userId;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");

        taskService = new TaskService();
        taskList = new ArrayList<>();

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        initViews();
        loadTasks();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, userRole, taskService);
        recyclerView.setAdapter(adapter);

        btnCreateTask = findViewById(R.id.btnCreateTask);
        if (userRole.equals("Employee")) {
            btnCreateTask.setVisibility(View.GONE);
        }
        btnCreateTask.setOnClickListener(v -> showCreateTaskDialog());
    }

    private void loadTasks() {
        com.google.firebase.database.Query tasksRef;
        if (userRole.equals("Manager")) {
            tasksRef = taskService.getTasksByManager(userId);
        } else {
            tasksRef = taskService.getTasksByEmployee(userId);
        }

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Task task = child.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void showCreateTaskDialog() {
        CreateTaskDialogFragment dialog = new CreateTaskDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateTask");
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

