package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.unifiedhr.system.R;
import com.unifiedhr.system.utils.FirebaseHelper;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            checkLoginStatus();
        }, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        String userRole = prefs.getString("userRole", null);
        
        // Check if user is logged in via SharedPreferences
        if (userId != null && userRole != null) {
            // Verify Firebase auth is still valid
            FirebaseAuth auth = FirebaseHelper.getInstance().getAuth();
            if (auth.getCurrentUser() != null) {
                // User is logged in, go directly to dashboard
                redirectToDashboard(userRole);
            } else {
                // Firebase session expired, clear SharedPreferences and go to login
                clearSession();
                goToLogin();
            }
        } else {
            // Not logged in, go to login
            goToLogin();
        }
    }

    private void redirectToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "Admin":
                intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                break;
            case "Manager":
                intent = new Intent(SplashActivity.this, ManagerDashboardActivity.class);
                break;
            case "Employee":
                intent = new Intent(SplashActivity.this, EmployeeDashboardActivity.class);
                break;
            case "JobSeeker":
                intent = new Intent(SplashActivity.this, JobSeekerDashboardActivity.class);
                break;
            default:
                goToLogin();
                return;
        }
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    private void clearSession() {
        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}


