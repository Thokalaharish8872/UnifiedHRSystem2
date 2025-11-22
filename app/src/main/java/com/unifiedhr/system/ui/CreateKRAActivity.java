package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.KRA;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.KRAService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateKRAActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etTarget, etDeadline;
    private Spinner spEmployees;
    private MaterialButton btnCreate;

    private UserService userService;
    private KRAService kraService;

    private List<User> assignableUsers = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private String companyId, userRole, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kra);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create KRA");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        companyId = prefs.getString("companyId", "");
        userRole = prefs.getString("userRole", "");
        userId = prefs.getString("userId", "");

        userService = new UserService();
        kraService = new KRAService(companyId);

        etTitle = findViewById(R.id.etKRATitle);
        etDescription = findViewById(R.id.etKRADescription);
        etTarget = findViewById(R.id.etKRATarget);
        etDeadline = findViewById(R.id.etKRADeadline);
        spEmployees = findViewById(R.id.spKRAEmployees);
        btnCreate = findViewById(R.id.btnCreateKRA);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spEmployees.setAdapter(adapter);

        loadAssignableUsers();
        btnCreate.setOnClickListener(v -> createKRA());
    }

    private void loadAssignableUsers() {
        assignableUsers.clear();
        adapter.clear();

        userService.getAllUsers()
                .orderByChild("companyId")
                .equalTo(companyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ch : snapshot.getChildren()) {
                            User u = ch.getValue(User.class);
                            if (u == null) continue;

                            // ADMIN → Employees + Managers
                            if (userRole.equalsIgnoreCase("Admin")) {

                                if (u.getRole().equalsIgnoreCase("Employee") ||
                                        u.getRole().equalsIgnoreCase("Manager")) {

                                    assignableUsers.add(u);
                                    adapter.add(u.getName() + " (" + u.getEmail() + ")");
                                }
                            }
                            // MANAGER → Employees only
                            else if (userRole.equalsIgnoreCase("Manager")) {

                                if (u.getRole().equalsIgnoreCase("Employee")) {
                                    assignableUsers.add(u);
                                    adapter.add(u.getName() + " (" + u.getEmail() + ")");
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (assignableUsers.isEmpty()) {
                            Toast.makeText(CreateKRAActivity.this, "No assignable users found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }


    private void createKRA() {

        String title = getStringValue(etTitle);
        String desc = getStringValue(etDescription);
        String target = getStringValue(etTarget);
        String deadline = getStringValue(etDeadline);

        if (title.isEmpty() || desc.isEmpty() || target.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int pos = spEmployees.getSelectedItemPosition();
        if (pos < 0 || pos >= assignableUsers.size()) {
            Toast.makeText(this, "Select a valid user", Toast.LENGTH_SHORT).show();
            return;
        }

        User assignee = assignableUsers.get(pos);

        KRA kra = new KRA(
                Utils.generateId(),
                companyId,
                assignee.getUserId(),
                title,
                desc,
                target,
                deadline
        );

        kra.setAssignedBy(userId);   // ⭐⭐ IMPORTANT ⭐⭐
        kra.setStatus("Active");
        kra.setCurrentProgress("0");

        kraService.createKRA(kra, (err, ref) -> {
            if (err == null) {
                Toast.makeText(this, "KRA created", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to create KRA", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getStringValue(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
