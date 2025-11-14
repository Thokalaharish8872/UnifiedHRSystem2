package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.TaskAdapter;
import com.unifiedhr.system.models.Task;
import com.unifiedhr.system.services.TaskService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");

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
        adapter = new TaskAdapter(taskList, userRole, this);
        recyclerView.setAdapter(adapter);

        btnCreateTask = findViewById(R.id.btnCreateTask);
        if ("Employee".equalsIgnoreCase(userRole)) {
            btnCreateTask.setVisibility(View.GONE);
        }
        btnCreateTask.setOnClickListener(v -> showCreateTaskDialog());
    }

    private void loadTasks() {
        Query tasksRef;
        if ("Manager".equalsIgnoreCase(userRole) || "Admin".equalsIgnoreCase(userRole)) {
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
    public void onUpdateStatus(Task task) {
        showUpdateStatusDialog(task);
    }

    private void showUpdateStatusDialog(Task task) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_task_status, null);
        Spinner spStatus = dialogView.findViewById(R.id.spStatus);
        TextInputEditText etNotes = dialogView.findViewById(R.id.etNotes);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.task_status_options, android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        int currentIndex = statusAdapter.getPosition(task.getStatus());
        if (currentIndex >= 0) {
            spStatus.setSelection(currentIndex);
        }
        etNotes.setText(task.getNotes());

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.action_update_task_status)
                .setView(dialogView)
                .setPositiveButton(R.string.submit, (dialog, which) -> {
                    String selectedStatus = spStatus.getSelectedItem().toString();
                    String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

                    task.setStatus(selectedStatus);
                    task.setNotes(notes);
                    if ("Completed".equalsIgnoreCase(selectedStatus)) {
                        task.setCompletedAt(System.currentTimeMillis());
                    } else {
                        task.setCompletedAt(0);
                    }

                    taskService.updateTask(task.getTaskId(), task, (error, ref) -> {
                        if (error == null) {
                            Toast.makeText(this, R.string.toast_task_update_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.toast_task_update_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
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
