package com.unifiedhr.system.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Company;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.services.CompanyService;
import com.unifiedhr.system.services.UserService;
import com.unifiedhr.system.utils.FirebaseHelper;
import com.unifiedhr.system.utils.Utils;

public class CreateCompanyDialogFragment extends DialogFragment {
    private EditText etCompanyName;
    private Button btnCreate;
    private CompanyService companyService;
    private UserService userService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_company, container, false);
        
        companyService = new CompanyService();
        userService = new UserService();
        
        etCompanyName = view.findViewById(R.id.etCompanyName);
        btnCreate = view.findViewById(R.id.btnCreate);
        
        btnCreate.setOnClickListener(v -> createCompany());
        
        return view;
    }

    private void createCompany() {
        String companyName = etCompanyName.getText().toString().trim();
        if (companyName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter company name", Toast.LENGTH_SHORT).show();
            return;
        }

        String companyId = Utils.generateId();
        String userId = FirebaseHelper.getInstance().getCurrentUserId();
        
        Company company = new Company(companyId, companyName, userId);
        company.setOfficeRadiusMeters(200);
        
        companyService.createCompany(company, (error, ref) -> {
            if (error == null) {
                // Update user with company ID
                SharedPreferences prefs = getActivity().getSharedPreferences("UnifiedHR", Context.MODE_PRIVATE);
                prefs.edit().putString("companyId", companyId).apply();
                
                userService.getUser(userId).child("companyId").setValue(companyId, (error1, ref1) -> {
                    if (error1 == null) {
                        Toast.makeText(getContext(), "Company created successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Failed to create company", Toast.LENGTH_SHORT).show();
            }
        });
    }
}





