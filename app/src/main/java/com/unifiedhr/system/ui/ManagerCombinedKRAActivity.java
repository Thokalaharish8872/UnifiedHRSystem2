package com.unifiedhr.system.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.KRAAdapter;
import com.unifiedhr.system.models.KRA;
import com.unifiedhr.system.services.KRAService;

import java.util.ArrayList;
import java.util.List;

public class ManagerCombinedKRAActivity extends AppCompatActivity {

    private RecyclerView rvYourKRA, rvAssignedKRA;
    private Button btnAssignNewKRA;

    private List<KRA> yourKraList = new ArrayList<>();
    private List<KRA> assignedKraList = new ArrayList<>();

    private KRAAdapter yourKraAdapter;
    private KRAAdapter assignedKraAdapter;

    private String userId;
    private String companyId;

    private KRAService kraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_combined_kra);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        companyId = prefs.getString("companyId", "");

        kraService = new KRAService(companyId);

        btnAssignNewKRA = findViewById(R.id.btnAssignNewKRA);
        rvYourKRA = findViewById(R.id.rvYourKRA);
        rvAssignedKRA = findViewById(R.id.rvAssignedKRA);

        rvYourKRA.setLayoutManager(new LinearLayoutManager(this));
        rvAssignedKRA.setLayoutManager(new LinearLayoutManager(this));

        yourKraAdapter = new KRAAdapter(yourKraList, true, false, null);
        assignedKraAdapter = new KRAAdapter(assignedKraList, false, false, null);

        rvYourKRA.setAdapter(yourKraAdapter);
        rvAssignedKRA.setAdapter(assignedKraAdapter);

        btnAssignNewKRA.setOnClickListener(v ->
                startActivity(new Intent(ManagerCombinedKRAActivity.this, CreateKRAActivity.class))
        );

        loadYourKRAs();
        loadAssignedKRAs();
    }

    private void loadYourKRAs() {
        yourKraList.clear();

        kraService.getKrasByEmployee(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            KRA kra = ds.getValue(KRA.class);
                            if (kra != null) yourKraList.add(kra);
                        }
                        yourKraAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadAssignedKRAs() {
        assignedKraList.clear();

        kraService.getKrasAssignedBy(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            KRA kra = ds.getValue(KRA.class);
                            if (kra != null) assignedKraList.add(kra);
                        }
                        assignedKraAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
