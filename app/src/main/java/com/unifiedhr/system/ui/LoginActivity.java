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
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnAdminRegister, btnSuperAdminRegister;
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
        btnAdminRegister = findViewById(R.id.btnRegister);
        btnSuperAdminRegister = findViewById(R.id.btnSuperAdminRegister);
        progressBar = findViewById(R.id.progressBar);

        Button btnJobSeekerLogin = findViewById(R.id.btnJobSeekerLogin);

        // Button actions
        btnLogin.setOnClickListener(v -> loginUser());
        btnAdminRegister.setOnClickListener(v -> showAdminRegisterDialog());
        btnSuperAdminRegister.setOnClickListener(v -> showSuperAdminDialog());
        btnJobSeekerLogin.setOnClickListener(v -> showJobSeekerLogin());
    }

    // -------------------------------------------------------
    // LOGIN FLOW
    // -------------------------------------------------------
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(
                                this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
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

                if (user == null) {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Admin approval logic
                if ("Admin".equals(user.getRole())) {

                    if ("pending".equals(user.getLoginStatus())) {
                        Toast.makeText(LoginActivity.this,
                                "Your admin request is pending approval.",
                                Toast.LENGTH_LONG).show();
                        auth.signOut();
                        return;
                    }

                    if ("rejected".equals(user.getLoginStatus())) {
                        Toast.makeText(LoginActivity.this,
                                "Your admin request was rejected.",
                                Toast.LENGTH_LONG).show();
                        auth.signOut();
                        return;
                    }
                }

                saveUserAndRedirect(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error loading user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -------------------------------------------------------
    // SUPER ADMIN REGISTRATION (Name + Company in ONE dialog)
    // -------------------------------------------------------
    private void showSuperAdminDialog() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_super_admin_register, null);

        TextInputLayout layoutName = view.findViewById(R.id.layoutSuperAdminName);
        TextInputEditText etName = view.findViewById(R.id.etSuperAdminName);

        TextInputLayout layoutCompany = view.findViewById(R.id.layoutCompanyName);
        TextInputEditText etCompany = view.findViewById(R.id.etCompanyName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Super Admin Registration")
                .setView(view)
                .setPositiveButton("Continue", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {

            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btn.setOnClickListener(v -> {

                String name = etName.getText().toString().trim();
                String company = etCompany.getText().toString().trim();

                if (name.isEmpty()) {
                    layoutName.setError("Name required");
                    return;
                } else layoutName.setError(null);

                if (company.isEmpty()) {
                    layoutCompany.setError("Company name required");
                    return;
                } else layoutCompany.setError(null);

                dialog.dismiss();
                createSuperAdmin(name, company);
            });
        });

        dialog.show();
    }

    private void createSuperAdmin(String name, String companyName) {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this,
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userId = FirebaseHelper.getInstance().getCurrentUserId();

                    // Create company
                    String companyId = FirebaseHelper.getInstance()
                            .getDatabaseReference("companies")
                            .push()
                            .getKey();

                    FirebaseHelper.getInstance()
                            .getDatabaseReference("companies")
                            .child(companyId)
                            .child("name")
                            .setValue(companyName);

                    // Create super admin
                    User user = new User(userId, email, name, "SuperAdmin", companyId);
                    user.setLoginStatus("approved");

                    userService.createUser(user, (error, ref) -> {

                        progressBar.setVisibility(View.GONE);

                        if (error == null) {
                            saveUserAndRedirect(user);
                            Toast.makeText(this,
                                    "Super Admin Registered Successfully!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this,
                                    "Failed saving profile",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

    // -------------------------------------------------------
    // ADMIN REGISTRATION (Name + Company)
    // -------------------------------------------------------
    private void showAdminRegisterDialog() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_admin_register, null);

        TextInputLayout layoutName = view.findViewById(R.id.layoutAdminName);
        TextInputEditText etName = view.findViewById(R.id.etAdminName);

        TextInputLayout layoutCompany = view.findViewById(R.id.layoutCompanyName);
        TextInputEditText etCompany = view.findViewById(R.id.etCompanyName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Admin Registration")
                .setView(view)
                .setPositiveButton("Continue", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {

            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btn.setOnClickListener(v -> {

                String name = etName.getText().toString().trim();
                String company = etCompany.getText().toString().trim();

                if (name.isEmpty()) {
                    layoutName.setError("Name required");
                    return;
                } else layoutName.setError(null);

                if (company.isEmpty()) {
                    layoutCompany.setError("Company required");
                    return;
                } else layoutCompany.setError(null);

                dialog.dismiss();
                checkCompanyForAdmin(name, company);
            });
        });

        dialog.show();
    }

    private void checkCompanyForAdmin(String name, String companyName) {

        FirebaseHelper.getInstance()
                .getDatabaseReference("companies")
                .orderByChild("name")
                .equalTo(companyName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            Toast.makeText(LoginActivity.this,
                                    "Company not found. Ask Super Admin to create company.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        String companyId = snapshot.getChildren().iterator().next().getKey();
                        createAdmin(name, companyId);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void createAdmin(String name, String companyId) {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this,
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userId = FirebaseHelper.getInstance().getCurrentUserId();

                    // Create admin user with pending status
                    User user = new User(userId, email, name, "Admin", companyId);
                    user.setLoginStatus("pending");

                    userService.createUser(user, (error, ref) -> {

                        if (error != null) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this,
                                    "Failed creating user",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create admin request under the company
                        String requestId = FirebaseHelper.getInstance()
                                .getDatabaseReference("companies")
                                .child(companyId)
                                .child("adminRequests")
                                .push()
                                .getKey();

                        AdminLoginRequest request =
                                new AdminLoginRequest(requestId, userId, email, name);

                        FirebaseHelper.getInstance()
                                .getDatabaseReference("companies")
                                .child(companyId)
                                .child("adminRequests")
                                .child(requestId)
                                .setValue(request);

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,
                                "Admin request sent. Super Admin must approve.",
                                Toast.LENGTH_LONG).show();

                        auth.signOut();
                    });
                });
    }

    // -------------------------------------------------------
    // JOB SEEKER LOGIN + REGISTRATION
    // -------------------------------------------------------
    private void showJobSeekerLogin() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        checkExistingJobSeeker();
                    } else {
                        registerJobSeeker(email, password);
                    }
                });
    }

    private void checkExistingJobSeeker() {

        String userId = FirebaseHelper.getInstance().getCurrentUserId();

        if (userId == null) {
            Toast.makeText(this,
                    "User ID not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        userService.getUser(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);

                        if (user != null) {
                            if ("JobSeeker".equals(user.getRole())) {
                                saveUserAndRedirect(user);
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "This is not a Job Seeker account.",
                                        Toast.LENGTH_SHORT).show();
                                auth.signOut();
                            }
                        } else {
                            askJobSeekerName();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void askJobSeekerName() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_input_name, null);

        TextInputLayout inputLayout = view.findViewById(R.id.inputLayoutName);
        TextInputEditText etName = view.findViewById(R.id.etName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter your name")
                .setView(view)
                .setPositiveButton("Continue", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {

            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btn.setOnClickListener(v -> {

                String name = etName.getText().toString().trim();

                if (name.isEmpty()) {
                    inputLayout.setError("Name required");
                    return;
                }

                inputLayout.setError(null);
                dialog.dismiss();
                createJobSeeker(name);
            });
        });

        dialog.show();
    }

    private void registerJobSeeker(String email, String password) {

        if (password.length() < 6) {
            Toast.makeText(this,
                    "Password must be 6+ characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        askJobSeekerName();
    }

    private void createJobSeeker(String name) {

        String userId = FirebaseHelper.getInstance().getCurrentUserId();

        if (userId == null || auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "User not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String email = auth.getCurrentUser().getEmail();

        User user = new User(userId, email, name, "JobSeeker", "");

        userService.createUser(user, (error, ref) -> {

            if (error == null) {
                saveUserAndRedirect(user);
            } else {
                Toast.makeText(this,
                        "Failed to create Job Seeker profile",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -------------------------------------------------------
    // SAVE + REDIRECT
    // -------------------------------------------------------
    private void saveUserAndRedirect(User user) {

        prefs.edit()
                .putString("userId", user.getUserId())
                .putString("userName", user.getName())
                .putString("userRole", user.getRole())
                .putString("companyId", user.getCompanyId())
                .apply();

        redirectToDashboard(user.getRole());
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
}
