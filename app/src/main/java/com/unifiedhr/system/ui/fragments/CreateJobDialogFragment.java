package com.unifiedhr.system.ui.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Company;
import com.unifiedhr.system.models.Job;
import com.unifiedhr.system.services.CompanyService;
import com.unifiedhr.system.services.RecruitmentService;
import com.unifiedhr.system.utils.Utils;

public class CreateJobDialogFragment extends DialogFragment {

    private TextInputEditText etTitle, etDescription, etDepartment, etLocation, etSkillsRequired;
    private Button btnCreate;
    private RecruitmentService recruitmentService;
    private CompanyService companyService;
    private String companyId, userId, companyName;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawableResource(android.R.color.white);
                WindowManager.LayoutParams params = window.getAttributes();
                params.dimAmount = 0.4f;
                window.setAttributes(params);
            }
        }
    }

    @Override
    public int getTheme() {
        return R.style.FullScreenDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_job, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UnifiedHR", android.content.Context.MODE_PRIVATE);
        companyId = prefs.getString("companyId", "");
        userId = prefs.getString("userId", "");

        recruitmentService = new RecruitmentService();
        companyService = new CompanyService();

        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        etDepartment = view.findViewById(R.id.etDepartment);
        etLocation = view.findViewById(R.id.etLocation);
        etSkillsRequired = view.findViewById(R.id.etSkillsRequired);
        btnCreate = view.findViewById(R.id.btnCreate);

        fetchCompanyName();
        btnCreate.setOnClickListener(v -> createJob());

        return view;
    }

    private void fetchCompanyName() {
        companyService.getCompany(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Company company = snapshot.getValue(Company.class);
                if (company != null) {
                    companyName = company.getName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createJob() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String skillsRequired = etSkillsRequired.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        String jobId = Utils.generateId();
        Job job = new Job(jobId, companyId, title, description, userId);
        job.setDepartment(department);
        job.setLocation(location);
        job.setSkillsRequired(skillsRequired);
        job.setCompanyName(companyName);

        recruitmentService.createJob(job, (error, ref) -> {
            if (error == null) {
                Toast.makeText(getContext(), "Job created successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to create job", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
