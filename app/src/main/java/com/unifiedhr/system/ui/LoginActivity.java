package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private UserService userService;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseHelper.getInstance().getAuth();
        userService = new UserService();
        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> registerAdmin());
        
        // Job Seeker login button
        Button btnJobSeekerLogin = findViewById(R.id.btnJobSeekerLogin);
        if (btnJobSeekerLogin != null) {
            btnJobSeekerLogin.setOnClickListener(v -> showJobSeekerLogin());
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        loadUserAndRedirect();
                    } else {
                        Toast.makeText(this, "Login failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserAndRedirect() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        userService.getUser(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    // Save user session in SharedPreferences
                    prefs.edit()
                            .putString("userId", user.getUserId())
                            .putString("userName", user.getName())
                            .putString("userRole", user.getRole())
                            .putString("companyId", user.getCompanyId())
                            .putString("employeeId", user.getEmployeeId())
                            .putBoolean("isRecruiter", user.isRecruiter())
                            .apply();

                    redirectToDashboard(user.getRole());
                } else {
                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerAdmin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {
                        String userId = FirebaseHelper.getInstance().getCurrentUserId();
                        String name = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;

                        User user = new User(userId, email, name, "Admin", "");
                        userService.createUser(user, (error, ref) -> {
                            if (error == null) {
                                Toast.makeText(this, "Admin account created successfully", Toast.LENGTH_SHORT).show();
                                loadUserAndRedirect();
                            } else {
                                Toast.makeText(this, "Failed to create user profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "Admin":
                intent = new Intent(this, AdminDashboardActivity.class);
                break;
            case "Manager":
                intent = new Intent(this, ManagerDashboardActivity.class);
                break;
            case "Employee":
                intent = new Intent(this, EmployeeDashboardActivity.class);
                break;
            case "JobSeeker":
                intent = new Intent(this, JobSeekerDashboardActivity.class);
                break;
            default:
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showJobSeekerLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        checkAndCreateJobSeeker();
                    } else {
                        // If login fails, try to register as JobSeeker
                        registerJobSeeker(email, password);
                    }
                });
    }

    private void checkAndCreateJobSeeker() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        userService.getUser(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if ("JobSeeker".equals(user.getRole())) {
                        saveSessionAndRedirect(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "This account is not a Job Seeker account", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                } else {
                    // User doesn't exist, create JobSeeker account
                    createJobSeekerAccount();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error checking user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerJobSeeker(String email, String password) {
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        createJobSeekerAccount();
                    } else {
                        Toast.makeText(this, "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createJobSeekerAccount() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null || auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String email = auth.getCurrentUser().getEmail();
        if (email == null) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String name = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;

        User user = new User(userId, email, name, "JobSeeker", "");
        userService.createUser(user, (error, ref) -> {
            if (error == null) {
                saveSessionAndRedirect(user);
            } else {
                Toast.makeText(this, "Failed to create Job Seeker profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSessionAndRedirect(User user) {
        prefs.edit()
                .putString("userId", user.getUserId())
                .putString("userName", user.getName())
                .putString("userRole", user.getRole())
                .putString("companyId", user.getCompanyId())
                .putString("employeeId", user.getEmployeeId())
                .putBoolean("isRecruiter", user.isRecruiter())
                .apply();

        redirectToDashboard(user.getRole());
    }
}
