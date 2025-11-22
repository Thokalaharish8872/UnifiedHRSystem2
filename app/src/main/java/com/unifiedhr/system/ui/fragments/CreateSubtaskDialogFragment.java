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
import java.util.*;

public class CreateSubtaskDialogFragment extends DialogFragment {

    private String parentTaskId, currentUserId, companyId;

    private TextInputEditText etTitle, etDescription, etDeadline;
    private TextInputLayout layoutAssignTo;
    private AppCompatSpinner spEmployees;
    private Button btnCreate;

    private TaskService taskService;
    private UserService userService;

    private final List<User> employees = new ArrayList<>();
    private ArrayAdapter<String> employeeAdapter;

    private final SimpleDateFormat dateFormatter =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static CreateSubtaskDialogFragment newInstance(String parentTaskId) {
        CreateSubtaskDialogFragment fragment = new CreateSubtaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("parentTaskId", parentTaskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_create_task, container, false);

        parentTaskId = getArguments().getString("parentTaskId");

        SharedPreferences prefs = requireActivity().getSharedPreferences(
                "UnifiedHR", android.content.Context.MODE_PRIVATE);

        currentUserId = prefs.getString("userId", "");
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

        loadEmployees();
        btnCreate.setOnClickListener(v -> createSubtask());

        return view;
    }

    private void loadEmployees() {
        employees.clear();
        employeeAdapter.clear();

        userService.getAllUsers()
                .orderByChild("managerId")
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user == null) return;

                            employees.add(user);
                            employeeAdapter.add(user.getName() + " (" + user.getEmail() + ")");
                        }

                        employeeAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void createSubtask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User employee = employees.get(spEmployees.getSelectedItemPosition());

        Task subtask = new Task(
                Utils.generateId(),
                title,
                description,
                employee.getUserId(),
                currentUserId,
                deadline,
                parentTaskId,
                "subtask",
                companyId
        );

        taskService.createTask(subtask, (error, ref) -> {
            if (error == null) {
                Toast.makeText(getContext(), "Subtask created", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
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
