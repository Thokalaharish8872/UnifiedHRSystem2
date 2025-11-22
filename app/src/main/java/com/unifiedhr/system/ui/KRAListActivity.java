package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.KRAAdapter;
import com.unifiedhr.system.models.KRA;
import com.unifiedhr.system.services.KRAService;

import java.util.ArrayList;
import java.util.List;

public class KRAListActivity extends AppCompatActivity {

    private RecyclerView rvKRA;
    private MaterialButton btnAddKRA;
    private SharedPreferences prefs;
    private KRAService kraService;

    private List<KRA> kraList = new ArrayList<>();
    private KRAAdapter adapter;

    private String companyId, userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kra_list);

        prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        companyId = prefs.getString("companyId", "");
        userRole = prefs.getString("userRole", "");

        kraService = new KRAService(companyId);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("KRA List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvKRA = findViewById(R.id.recyclerView);
        btnAddKRA = findViewById(R.id.btnAddKRA);

        rvKRA.setLayoutManager(new LinearLayoutManager(this));

        boolean isAdmin = userRole.equalsIgnoreCase("Admin");
        boolean isManager = userRole.equalsIgnoreCase("Manager");
        boolean isEmployee = userRole.equalsIgnoreCase("Employee");

        // CORRECT FLAG HANDLING
        boolean showUpdateButton = isManager || isEmployee;
        boolean allowEditDelete = isAdmin;

        KRAAdapter.KRAActionListener listener = new KRAAdapter.KRAActionListener() {
            @Override
            public void onEdit(KRA kra) {
                // Admin only
            }

            @Override
            public void onDelete(KRA kra) {
                kraService.deleteKRA(kra.getKraId(), (error, ref) -> {
                    if (error == null) {
                        loadKRAs();
                    }
                });
            }
        };

        adapter = new KRAAdapter(kraList, showUpdateButton, allowEditDelete, listener);
        rvKRA.setAdapter(adapter);

        if (isAdmin || isManager) {
            btnAddKRA.setVisibility(View.VISIBLE);
            btnAddKRA.setOnClickListener(v ->
                    startActivity(new Intent(this, CreateKRAActivity.class))
            );
        } else {
            btnAddKRA.setVisibility(View.GONE);
        }

        loadKRAs();
    }

    private void loadKRAs() {
        kraList.clear();

        kraService.getAllKrasByCompany(companyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ch : snapshot.getChildren()) {
                            KRA k = ch.getValue(KRA.class);
                            if (k != null) kraList.add(k);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
