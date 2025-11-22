package com.unifiedhr.system.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.AdminLoginRequest;
import com.unifiedhr.system.models.Company;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.CompanyService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;

public class AdminRegistrationActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etCompanyName;
    private Button btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private UserService userService;
    private CompanyService companyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);

        auth = FirebaseHelper.getInstance().getAuth();
        userService = new UserService();
        companyService = new CompanyService();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCompanyName = findViewById(R.id.etCompanyName);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> registerAdmin());
    }

    private void registerAdmin() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String companyName = etCompanyName.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || companyName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        String companyId = FirebaseHelper.getInstance().getDatabaseReference("companies").push().getKey();

                        Company company = new Company(companyId, companyName, userId);
                        companyService.createCompany(company, (databaseError, databaseReference) -> {
                            if (databaseError != null) {
                                Toast.makeText(AdminRegistrationActivity.this, "Failed to create company.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });

                        User user = new User(userId, email, name, "Admin", companyId);
                        user.setLoginStatus("pending");

                        userService.createUser(user, (databaseError, databaseReference) -> {
                            progressBar.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);

                            if (databaseError == null) {
                                Toast.makeText(AdminRegistrationActivity.this, "Registration request sent. Please wait for approval.", Toast.LENGTH_LONG).show();
                                auth.signOut();
                                finish();
                            } else {
                                Toast.makeText(AdminRegistrationActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(AdminRegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
