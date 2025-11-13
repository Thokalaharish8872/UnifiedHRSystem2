package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.KRAAdapter;
import com.unifiedhr.system.models.KRA;
import com.unifiedhr.system.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class KRAActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private KRAAdapter adapter;
    private List<KRA> kraList;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kra);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        employeeId = prefs.getString("employeeId", "");

        kraList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        initViews();
        loadKRAs();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KRAAdapter(kraList);
        recyclerView.setAdapter(adapter);
    }

    private void loadKRAs() {
        Query kraRef = FirebaseHelper.getInstance().getDatabaseReference("kras")
            .orderByChild("employeeId").equalTo(employeeId);
        
        kraRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                kraList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    KRA kra = child.getValue(KRA.class);
                    if (kra != null) {
                        kraList.add(kra);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
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

