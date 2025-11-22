package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.KRAAdapter;
import com.unifiedhr.system.models.KRA;
import com.unifiedhr.system.services.KRAService;

import java.util.ArrayList;
import java.util.List;

public class KRAActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KRAAdapter adapter;
    private List<KRA> kraList;

    private String userId;
    private String userRole;
    private String companyId;

    private KRAService kraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kra);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("My KRA");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userRole = prefs.getString("userRole", "");
        companyId = prefs.getString("companyId", "");

        kraList = new ArrayList<>();
        kraService = new KRAService(companyId);

        initViews();
        loadKRAs();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        boolean showUpdateForEmployee = userRole.equalsIgnoreCase("Employee");
        boolean allowEditDelete = false; // employee cannot edit or delete

        adapter = new KRAAdapter(
                kraList,
                showUpdateForEmployee,
                allowEditDelete,
                new KRAAdapter.KRAActionListener() {
                    @Override
                    public void onEdit(KRA kra) {
                        // Employee has no edit
                        Toast.makeText(KRAActivity.this, "You cannot edit KRA", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDelete(KRA kra) {
                        // Employee has no delete
                        Toast.makeText(KRAActivity.this, "You cannot delete KRA", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        recyclerView.setAdapter(adapter);
    }

    private void loadKRAs() {
        kraService.getKrasByEmployee(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        kraList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            KRA kra = child.getValue(KRA.class);
                            if (kra != null) kraList.add(kra);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
