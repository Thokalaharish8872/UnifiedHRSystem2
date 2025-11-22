package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.TaskAdapter;
import com.unifiedhr.system.models.Task;
import com.unifiedhr.system.services.TaskService;
import com.unifiedhr.system.ui.fragments.CreateSubtaskDialogFragment;
import com.unifiedhr.system.ui.fragments.CreateTaskDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity implements TaskAdapter.TaskActionListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();
    private TaskService taskService;

    private Button btnCreateTask;

    private String userId;
    private String userRole;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");
        companyId = prefs.getString("companyId", "");   // ADDED

        taskService = new TaskService();

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

        adapter = new TaskAdapter(taskList, userRole, userId, this);
        recyclerView.setAdapter(adapter);

        btnCreateTask = findViewById(R.id.btnCreateTask);

        if ("Employee".equalsIgnoreCase(userRole)) {
            btnCreateTask.setVisibility(View.GONE);
        }

        btnCreateTask.setOnClickListener(v -> showCreateTaskDialog());
    }

    private void loadTasks() {

        taskService.getAllTasks().addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {

                taskList.clear();

                for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                    Task task = child.getValue(Task.class);
                    if (task == null) continue;

                    if (!companyId.equals(task.getCompanyId())) {
                        continue;
                    }

                    if (isTaskVisible(task)) {
                        taskList.add(task);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(TaskManagementActivity.this, "Failed to load tasks.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isTaskVisible(Task task) {

        if ("Admin".equalsIgnoreCase(userRole)) {
            return TextUtils.isEmpty(task.getParentTaskId());
        }

        if ("Manager".equalsIgnoreCase(userRole)) {

            boolean assignedToManager = userId.equals(task.getAssignedTo()) &&
                    TextUtils.isEmpty(task.getParentTaskId());

            boolean createdByManagerSubtask = userId.equals(task.getAssignedBy()) &&
                    !TextUtils.isEmpty(task.getParentTaskId());

            return assignedToManager || createdByManagerSubtask;
        }

        if ("Employee".equalsIgnoreCase(userRole)) {
            return userId.equals(task.getAssignedTo());
        }

        return false;
    }

    private void showCreateTaskDialog() {
        new CreateTaskDialogFragment().show(
                getSupportFragmentManager(), "CreateTask"
        );
    }

    @Override
    public void onUpdateStatus(Task task) {
        showUpdateStatusDialog(task);
    }

    @Override
    public void onCreateSubtask(Task parentTask) {
        CreateSubtaskDialogFragment fragment =
                CreateSubtaskDialogFragment.newInstance(parentTask.getTaskId());
        fragment.show(getSupportFragmentManager(), "CreateSubtask");
    }

    private void showUpdateStatusDialog(Task task) {

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_update_task_status, null);

        Spinner spStatus = dialogView.findViewById(R.id.spStatus);
        TextInputEditText etNotes = dialogView.findViewById(R.id.etNotes);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.task_status_options,
                android.R.layout.simple_spinner_dropdown_item
        );
        spStatus.setAdapter(statusAdapter);

        int index = statusAdapter.getPosition(task.getStatus());
        if (index >= 0) spStatus.setSelection(index);

        etNotes.setText(task.getNotes());

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Status")
                .setView(dialogView)
                .setPositiveButton("Submit", (d, w) -> {

                    task.setStatus(spStatus.getSelectedItem().toString());
                    task.setNotes(etNotes.getText() != null ? etNotes.getText().toString() : "");

                    if ("Completed".equalsIgnoreCase(task.getStatus())) {
                        task.setCompletedAt(System.currentTimeMillis());
                    } else {
                        task.setCompletedAt(0);
                    }

                    taskService.updateTask(task, (error, ref) -> {
                        if (error == null) {
                            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
