package com.unifiedhr.system.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Task;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.TaskService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateTaskDialogFragment extends DialogFragment {
    private EditText etTitle, etDescription, etDeadline;
    private Button btnCreate;
    private TaskService taskService;
    private UserService userService;
    private String managerId;
    private List<User> employees;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_task, container, false);
        
        SharedPreferences prefs = getActivity().getSharedPreferences("UnifiedHR", android.content.Context.MODE_PRIVATE);
        managerId = prefs.getString("userId", "");
        
        taskService = new TaskService();
        userService = new UserService();
        employees = new ArrayList<>();
        
        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        etDeadline = view.findViewById(R.id.etDeadline);
        btnCreate = view.findViewById(R.id.btnCreate);
        
        loadEmployees();
        btnCreate.setOnClickListener(v -> createTask());
        
        return view;
    }

    private void loadEmployees() {
        userService.getAllUsers().orderByChild("managerId").equalTo(managerId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            employees.add(user);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
    }

    private void createTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (employees.isEmpty()) {
            Toast.makeText(getContext(), "No employees available", Toast.LENGTH_SHORT).show();
            return;
        }

        // For simplicity, assign to first employee
        String assignedTo = employees.get(0).getUserId();
        
        Task task = new Task();
        task.setTaskId(Utils.generateId());
        task.setTitle(title);
        task.setDescription(description);
        task.setAssignedTo(assignedTo);
        task.setAssignedBy(managerId);
        task.setDeadline(deadline);

        taskService.createTask(task, (error, ref) -> {
            if (error == null) {
                Toast.makeText(getContext(), "Task created successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to create task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}








