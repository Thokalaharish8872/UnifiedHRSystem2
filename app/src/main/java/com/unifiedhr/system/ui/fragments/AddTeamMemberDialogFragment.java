package com.unifiedhr.system.ui.fragments;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.CompanyService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;
import com.unifiedhr.system.utils.Utils;

public class AddTeamMemberDialogFragment extends DialogFragment {
    private EditText etEmail, etName, etPassword;
    private Button btnAdd;
    private UserService userService;
    private CompanyService companyService;
    private String managerId;
    private String companyId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_team_member, container, false);
        
        SharedPreferences prefs = getActivity().getSharedPreferences("UnifiedHR", Context.MODE_PRIVATE);
        managerId = prefs.getString("userId", "");
        companyId = prefs.getString("companyId", "");
        
        userService = new UserService();
        companyService = new CompanyService();
        
        etEmail = view.findViewById(R.id.etEmail);
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        btnAdd = view.findViewById(R.id.btnAdd);
        
        btnAdd.setOnClickListener(v -> addTeamMember());
        
        return view;
    }

    private void addTeamMember() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseHelper.getInstance().getAuth();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = task.getResult().getUser().getUid();
                String employeeId = Utils.generateEmployeeId(companyId, 0); // Should get actual count
                
                User user = new User(userId, email, name, "Employee", companyId);
                user.setEmployeeId(employeeId);
                user.setManagerId(managerId);
                
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








