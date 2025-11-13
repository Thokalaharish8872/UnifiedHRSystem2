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
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;

public class AddManagerDialogFragment extends DialogFragment {
    private EditText etEmail, etName, etPassword;
    private Button btnAdd;
    private UserService userService;
    private String companyId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_team_member, container, false);
        
        SharedPreferences prefs = getActivity().getSharedPreferences("UnifiedHR", Context.MODE_PRIVATE);
        companyId = prefs.getString("companyId", "");
        
        userService = new UserService();
        
        etEmail = view.findViewById(R.id.etEmail);
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        btnAdd = view.findViewById(R.id.btnAdd);
        
        btnAdd.setOnClickListener(v -> addManager());
        
        return view;
    }

    private void addManager() {
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
                
                User user = new User(userId, email, name, "Manager", companyId);
                
                userService.createUser(user, (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(getContext(), "Manager added successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to add manager", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Failed to create user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}








