package com.unifiedhr.system.ui.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Task;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.TaskService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateTaskDialogFragment extends DialogFragment {
    private TextInputEditText etTitle, etDescription, etDeadline;
    private TextInputLayout layoutAssignTo;
    private AppCompatSpinner spEmployees;
    private Button btnCreate;
    private TaskService taskService;
    private UserService userService;
    private String currentUserId;
    private String managerId;
    private String userRole;
    private String companyId;
    private final List<User> employees = new ArrayList<>();
    private ArrayAdapter<String> employeeAdapter;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_task, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UnifiedHR", android.content.Context.MODE_PRIVATE);
        currentUserId = prefs.getString("userId", "");
        managerId = currentUserId;
        userRole = prefs.getString("userRole", "");
        companyId = prefs.getString("companyId", "");

        taskService = new TaskService();
        userService = new UserService();

        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        etDeadline = view.findViewById(R.id.etDeadline);
        layoutAssignTo = view.findViewById(R.id.layoutAssignTo);
        spEmployees = view.findViewById(R.id.spEmployees);
        btnCreate = view.findViewById(R.id.btnCreate);

        employeeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spEmployees.setAdapter(employeeAdapter);

        etDeadline.setOnClickListener(v -> showDatePicker());

        loadEmployees();
        btnCreate.setOnClickListener(v -> createTask());

        return view;
    }

    private void loadEmployees() {
        employees.clear();
        employeeAdapter.clear();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employees.clear();
                employeeAdapter.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user == null) continue;

                    boolean include;
                    if ("Admin".equalsIgnoreCase(userRole)) {
                        include = user.getRole() != null &&
                                (user.getRole().equalsIgnoreCase("Employee") || user.getRole().equalsIgnoreCase("Manager"));
                    } else {
                        include = managerId.equals(user.getManagerId());
                    }

                    if (include) {
                        employees.add(user);
                        String displayName = user.getName() + " (" + user.getEmail() + ")";
                        employeeAdapter.add(displayName);
                    }
                }
                employeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        if ("Admin".equalsIgnoreCase(userRole)) {
            if (TextUtils.isEmpty(companyId)) {
                userService.getAllUsers()
                        .addListenerForSingleValueEvent(listener);
            } else {
                userService.getAllUsers()
                        .orderByChild("companyId")
                        .equalTo(companyId)
                        .addListenerForSingleValueEvent(listener);
            }
        } else {
            userService.getAllUsers()
                    .orderByChild("managerId")
                    .equalTo(managerId)
                    .addListenerForSingleValueEvent(listener);
        }
    }

    private void createTask() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String deadline = etDeadline.getText() != null ? etDeadline.getText().toString().trim() : "";

        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (employees.isEmpty()) {
            Toast.makeText(getContext(), "No employees available", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = spEmployees.getSelectedItemPosition();
        if (position < 0 || position >= employees.size()) {
            layoutAssignTo.setError(getString(R.string.error_assign_employee_required));
            return;
        } else {
            layoutAssignTo.setError(null);
        }

        User assignee = employees.get(position);

        Task task = new Task();
        task.setTaskId(Utils.generateId());
        task.setTitle(title);
        task.setDescription(description);
        task.setAssignedTo(assignee.getUserId());
        task.setAssignedBy(currentUserId);
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

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etDeadline.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}








