package com.unifiedhr.system.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.CompanyService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;
import com.unifiedhr.system.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AddTeamMemberDialogFragment extends DialogFragment {
    private EditText etEmail, etName, etPassword;
    private Button btnAdd;
    private Spinner spManager;
    private View layoutManagerSelector;
    private UserService userService;
    private CompanyService companyService;
    private String selectedManagerId;
    private String companyId;
    private String userRole;
    private String currentUserId;
    private ArrayAdapter<String> managerAdapter;
    private final List<User> managerList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_team_member, container, false);
        
        SharedPreferences prefs = getActivity().getSharedPreferences("UnifiedHR", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");
        companyId = prefs.getString("companyId", "");
        
        userService = new UserService();
        companyService = new CompanyService();
        
        etEmail = view.findViewById(R.id.etEmail);
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        btnAdd = view.findViewById(R.id.btnAdd);
        spManager = view.findViewById(R.id.spManager);
        layoutManagerSelector = view.findViewById(R.id.layoutManagerSelector);

        setupManagerSelector();
        
        btnAdd.setOnClickListener(v -> addTeamMember());
        
        return view;
    }

    private void setupManagerSelector() {
        managerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>());
        managerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spManager.setAdapter(managerAdapter);
        managerAdapter.add(getString(R.string.select_manager_prompt));
        spManager.setSelection(0);

        spManager.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedManagerId = null;
                    if ("Admin".equalsIgnoreCase(userRole)) {
                        btnAdd.setEnabled(false);
                    }
                    return;
                }
                if (position - 1 < managerList.size()) {
                    selectedManagerId = managerList.get(position - 1).getUserId();
                    btnAdd.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedManagerId = null;
                if ("Admin".equalsIgnoreCase(userRole)) {
                    btnAdd.setEnabled(false);
                }
            }
        });

        if ("Admin".equalsIgnoreCase(userRole)) {
            layoutManagerSelector.setVisibility(View.VISIBLE);
            btnAdd.setEnabled(false);
            loadManagers();
        } else {
            layoutManagerSelector.setVisibility(View.GONE);
            selectedManagerId = currentUserId;
            btnAdd.setEnabled(true);
        }
    }

    private void loadManagers() {
        if (TextUtils.isEmpty(companyId)) {
            Toast.makeText(getContext(), R.string.error_company_not_found, Toast.LENGTH_SHORT).show();
            btnAdd.setEnabled(false);
            return;
        }

        userService.getAllUsers()
                .orderByChild("companyId")
                .equalTo(companyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        managerList.clear();
                        managerAdapter.clear();
                        managerAdapter.add(getString(R.string.select_manager_prompt));
                        selectedManagerId = null;
                        btnAdd.setEnabled(false);

                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null && "Manager".equalsIgnoreCase(user.getRole())) {
                                managerList.add(user);
                                managerAdapter.add(user.getName());
                            }
                        }
                        managerAdapter.notifyDataSetChanged();

                        if (managerList.isEmpty()) {
                            Toast.makeText(getContext(),
                                    R.string.error_no_managers_available,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                R.string.error_loading_managers,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addTeamMember() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (companyId == null || companyId.trim().isEmpty()) {
            Toast.makeText(getContext(), "Company information not available. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedManagerId)) {
            Toast.makeText(getContext(), R.string.error_select_manager, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseHelper.getInstance().getAuth();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = task.getResult().getUser().getUid();
                String employeeId = Utils.generateEmployeeId(companyId, 0); // Should get actual count
                
                User user = new User(userId, email, name, "Employee", companyId);
                user.setEmployeeId(employeeId);
                user.setManagerId(selectedManagerId);
                
                userService.createUser(user, (error, ref) -> {
                    if (error == null) {
                        companyService.incrementEmployeeCount(companyId, null);
                        Toast.makeText(getContext(), "Team member added successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to add team member", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Failed to create user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}








