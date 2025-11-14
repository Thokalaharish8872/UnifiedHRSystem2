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

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::checkLoginStatus, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        String userRole = prefs.getString("userRole", null);

        if (userId != null && userRole != null) {

            FirebaseAuth auth = FirebaseHelper.getInstance().getAuth();
            if (auth.getCurrentUser() != null) {
                redirectToDashboard(userRole);
                return;
            }

            clearUserData();
        }

        goToLogin();
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
                goToLogin();
                return;
        }

        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void clearUserData() {
        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
