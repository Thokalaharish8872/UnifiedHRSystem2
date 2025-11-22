package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private Button btnLogin, btnAdminRegister, btnJobSeekerRegister;
    private ProgressBar progressBar;
    private TextView tvForgotPassword;
    private FirebaseAuth auth;
    private UserService userService;
    private SharedPreferences prefs;
    private long backPressedTime;
    private Toast backToast;

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
        btnJobSeekerRegister = findViewById(R.id.btnJobSeekerRegister);
        progressBar = findViewById(R.id.progressBar);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> loginUser());
        btnAdminRegister.setOnClickListener(v -> startActivity(new Intent(this, AdminRegistrationActivity.class)));
        btnJobSeekerRegister.setOnClickListener(v -> askJobSeekerName());
        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

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
                        Toast.makeText(this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserAndRedirect() {
        String userId = FirebaseHelper.getInstance().getCurrentUserId();

        if (userId == null) {
            Toast.makeText(this, "User ID not found after login.", Toast.LENGTH_SHORT).show();
            return;
        }

        userService.getUser(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user == null) {
                    Toast.makeText(LoginActivity.this, "User data not found in database. Please contact support.", Toast.LENGTH_LONG).show();
                    auth.signOut();
                    return;
                }

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
                Toast.makeText(LoginActivity.this,
                        "Error loading user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            finishAffinity();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
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

                    User user = new User(userId, email, name, "Admin", companyId);
                    user.setLoginStatus("pending");

                    userService.createUser(user, (error, ref) -> {

                        if (error != null) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Failed creating user", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String requestId = FirebaseHelper.getInstance()
                                .getDatabaseReference("companies")
                                .child(companyId)
                                .child("adminRequests")
                                .push()
                                .getKey();

                        AdminLoginRequest req =
                                new AdminLoginRequest(requestId, userId, email, name);

                        FirebaseHelper.getInstance()
                                .getDatabaseReference("companies")
                                .child(companyId)
                                .child("adminRequests")
                                .child(requestId)
                                .setValue(req);

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,
                                "Admin request sent. Waiting for approval.",
                                Toast.LENGTH_LONG).show();

                        auth.signOut();
                    });
                });
    }

    private void askJobSeekerName() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_input_name, null);

        TextInputLayout layoutName = view.findViewById(R.id.inputLayoutName);
        TextInputEditText etName = view.findViewById(R.id.etName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter Your Name")
                .setView(view)
                .setPositiveButton("Register", null)
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btn.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();

                if (name.isEmpty()) {
                    layoutName.setError("Name required");
                    return;
                }

                dialog.dismiss();
                createJobSeeker(name);
            });
        });

        dialog.show();
    }

    private void createJobSeeker(String name) {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email & password first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be 6+ characters", Toast.LENGTH_SHORT).show();
            return;
        }

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

                    User user = new User(userId, email, name, "JobSeeker", "");

                    userService.createUser(user, (error, ref) -> {
                        progressBar.setVisibility(View.GONE);

                        if (error == null) {
                            saveUserAndRedirect(user);
                        } else {
                            Toast.makeText(this,
                                    "Failed to save profile",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

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
                Toast.makeText(this, "Unknown role!", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish();
    }

    private void showForgotPasswordDialog() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_input_name, null);

        TextInputLayout inputLayout = view.findViewById(R.id.inputLayoutName);
        inputLayout.setHint("Enter your email");

        TextInputEditText etEmail = view.findViewById(R.id.etName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setView(view)
                .setPositiveButton("Send Reset Link", null)
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(dlg -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btn.setOnClickListener(v -> {

                String email = etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    inputLayout.setError("Email required");
                    return;
                }

                sendResetEmail(email);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void sendResetEmail(String email) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Reset link sent to your email.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
