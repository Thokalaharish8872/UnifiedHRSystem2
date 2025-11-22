package com.unifiedhr.system.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unifiedhr.system.R;
import com.unifiedhr.system.services.KRAService;

public class UpdateKRAProgressActivity extends AppCompatActivity {

    private TextInputEditText etNewProgress;
    private MaterialButton btnSubmit;
    private String kraId, companyId;
    private KRAService kraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_kra_progress);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Progress");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        kraId = getIntent().getStringExtra("kraId");
        companyId = getIntent().getStringExtra("companyId");
        if (companyId == null) companyId = getSharedPreferences("UnifiedHR", MODE_PRIVATE).getString("companyId", "");

        kraService = new KRAService(companyId);

        etNewProgress = findViewById(R.id.etNewProgress);
        btnSubmit = findViewById(R.id.btnSubmitProgress);

        btnSubmit.setOnClickListener(v -> {
            String val = etNewProgress.getText() == null ? "" : etNewProgress.getText().toString().trim();
            if (val.isEmpty()) {
                Toast.makeText(this, "Enter progress", Toast.LENGTH_SHORT).show();
                return;
            }
            kraService.updateProgress(kraId, val, (err, ref) -> {
                if (err == null) {
                    Toast.makeText(this, "Progress updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
