package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.AdminLoginRequest;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.AdminLoginRequestService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private UserService userService;
    private AdminLoginRequestService requestService;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseHelper.getInstance().getAuth();
        userService = new UserService();
        requestService = new AdminLoginRequestService();
        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        Button btnJobSeekerLogin = findViewById(R.id.btnJobSeekerLogin);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> applyForAdminLogin());

        btnJobSeekerLogin.setOnClickListener(v -> showJobSeekerLogin());
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
                    // Check if admin login is approved
                    if ("Admin".equals(user.getRole())) {
                        String loginStatus = user.getLoginStatus();
                        if (loginStatus == null || "pending".equals(loginStatus)) {
                            Toast.makeText(LoginActivity.this, "Your admin login request is pending approval. Please wait for Super Admin approval.", Toast.LENGTH_LONG).show();
                            auth.signOut();
                            return;
                        } else if ("rejected".equals(loginStatus)) {
                            Toast.makeText(LoginActivity.this, "Your admin login request has been rejected. Please contact Super Admin.", Toast.LENGTH_LONG).show();
                            auth.signOut();
                            return;
                        }
                    }

                    prefs.edit()
                            .putString("userId", user.getUserId())
                            .putString("userName", user.getName())
                            .putString("userRole", user.getRole())
                            .putString("companyId", user.getCompanyId())
                            .putString("employeeId", user.getEmployeeId())
                            .putString("managerId", user.getManagerId())
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

    private void applyForAdminLogin() {
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

        promptForName(getString(R.string.dialog_title_admin_name),
                name -> createAdminLoginRequest(email, password, name));
    }

    private void createAdminLoginRequest(String email, String password, String name) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnRegister.setEnabled(false);

        // First create Firebase Auth account
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = FirebaseHelper.getInstance().getCurrentUserId();
                        if (userId == null) {
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            btnRegister.setEnabled(true);
                            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create user with pending status
                        User user = new User(userId, email, name, "Admin", "");
                        user.setLoginStatus("pending");
                        userService.createUser(user, (userError, userRef) -> {
                            if (userError == null) {
                                // Create admin login request
                                String requestId = FirebaseHelper.getInstance().getDatabaseReference("adminLoginRequests").push().getKey();
                                if (requestId != null) {
                                    AdminLoginRequest request = new AdminLoginRequest(requestId, userId, email, name);
                                    requestService.createRequest(request, (requestError, requestRef) -> {
                                        progressBar.setVisibility(View.GONE);
                                        btnLogin.setEnabled(true);
                                        btnRegister.setEnabled(true);

                                        if (requestError == null) {
                                            Toast.makeText(this, "Admin login request submitted. Please wait for Super Admin approval.", Toast.LENGTH_LONG).show();
                                            auth.signOut();
                                        } else {
                                            Toast.makeText(this, "Failed to create login request", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                    btnRegister.setEnabled(true);
                                    Toast.makeText(this, "Failed to create request ID", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                btnRegister.setEnabled(true);
                                Toast.makeText(this, "Failed to create user profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        btnRegister.setEnabled(true);
                        Toast.makeText(this, "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "SuperAdmin":
                intent = new Intent(this, SuperAdminDashboardActivity.class);
                break;
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
                        saveUserDataAndRedirect(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "This account is not a Job Seeker account", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                } else {
                    promptForName(getString(R.string.dialog_title_job_seeker_name),
                            LoginActivity.this::createJobSeekerAccount);
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

        promptForName(getString(R.string.dialog_title_job_seeker_name),
                name -> createJobSeekerWithAuth(email, password, name));
    }

    private void createJobSeekerWithAuth(String email, String password, String name) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        createJobSeekerAccount(name);
                    } else {
                        Toast.makeText(this, "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createJobSeekerAccount(String name) {
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

        User user = new User(userId, email, name, "JobSeeker", "");
        userService.createUser(user, (error, ref) -> {
            if (error == null) {
                saveUserDataAndRedirect(user);
            } else {
                Toast.makeText(this, "Failed to create Job Seeker profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataAndRedirect(User user) {
        prefs.edit()
                .putString("userId", user.getUserId())
                .putString("userName", user.getName())
                .putString("userRole", user.getRole())
                .putString("companyId", user.getCompanyId())
                .putString("employeeId", user.getEmployeeId())
                .putString("managerId", user.getManagerId())
                .putBoolean("isRecruiter", user.isRecruiter())
                .apply();

        redirectToDashboard(user.getRole());
    }

    private void promptForName(String title, NameCallback callback) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null);
        TextInputLayout inputLayout = dialogView.findViewById(R.id.inputLayoutName);
        TextInputEditText etName = dialogView.findViewById(R.id.etName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(R.string.action_continue, null)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                if (name.isEmpty()) {
                    inputLayout.setError(getString(R.string.error_name_required));
                } else {
                    inputLayout.setError(null);
                    dialog.dismiss();
                    callback.onNameEntered(name);
                }
            });
        });

        dialog.show();
    }

    private interface NameCallback {
        void onNameEntered(String name);
    }
}
