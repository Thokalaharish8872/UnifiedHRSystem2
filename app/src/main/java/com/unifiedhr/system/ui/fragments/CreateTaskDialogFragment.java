package com.unifiedhr.system.ui.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    private String currentUserId, userRole, companyId;

    private final List<User> employees = new ArrayList<>();
    private ArrayAdapter<String> employeeAdapter;

    private final SimpleDateFormat dateFormatter =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {

            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.80);

            getDialog().getWindow().setLayout(
                    width,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_create_task, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences(
                "UnifiedHR", android.content.Context.MODE_PRIVATE);

        currentUserId = prefs.getString("userId", "");
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

        employeeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>()
        );
        spEmployees.setAdapter(employeeAdapter);

        etDeadline.setOnClickListener(v -> showDatePicker());

        loadUsers();
        btnCreate.setOnClickListener(v -> createTask());

        return view;
    }

    private void loadUsers() {
        employees.clear();
        employeeAdapter.clear();

        if ("Admin".equalsIgnoreCase(userRole)) {
            userService.getAllUsers()
                    .orderByChild("companyId")
                    .equalTo(companyId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                User user = child.getValue(User.class);
                                if (user != null && "Manager".equals(user.getRole())) {
                                    employees.add(user);
                                    employeeAdapter.add(user.getName() + " (" + user.getEmail() + ")");
                                }
                            }
                            employeeAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        } else if ("Manager".equalsIgnoreCase(userRole)) {
            userService.getAllUsers()
                    .orderByChild("managerId")
                    .equalTo(currentUserId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                User user = child.getValue(User.class);
                                if (user != null) {
                                    employees.add(user);
                                    employeeAdapter.add(user.getName() + " (" + user.getEmail() + ")");
                                }
                            }
                            employeeAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    private void createTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (employees.isEmpty()) {
            Toast.makeText(getContext(), "No users available to assign tasks", Toast.LENGTH_SHORT).show();
            return;
        }

        User assignee = employees.get(spEmployees.getSelectedItemPosition());

        Task task = new Task(
                Utils.generateId(),
                title,
                description,
                assignee.getUserId(),
                currentUserId,
                deadline,
                null,
                "admin_task",
                companyId
        );

        taskService.createTask(task, (error, ref) -> {
            if (error == null) {
                Toast.makeText(getContext(), "Task created", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> {
                    c.set(y, m, d);
                    etDeadline.setText(dateFormatter.format(c.getTime()));
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }
}
