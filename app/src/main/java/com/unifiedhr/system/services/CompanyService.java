package com.unifiedhr.system.services;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.unifiedhr.system.models.Company;
import com.unifiedhr.system.utils.FirebaseHelper;

public class CompanyService {
    private DatabaseReference companiesRef;

    public CompanyService() {
        companiesRef = FirebaseHelper.getInstance().getDatabaseReference("companies");
    }

    public void createCompany(Company company, DatabaseReference.CompletionListener listener) {
        companiesRef.child(company.getCompanyId()).setValue(company, listener);
    }

    public DatabaseReference getCompany(String companyId) {
        return companiesRef.child(companyId);
    }

    public void incrementEmployeeCount(String companyId, DatabaseReference.CompletionListener listener) {
        if (companyId == null) return;
        companiesRef.child(companyId).child("employeeCount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (listener != null) {
                    listener.onComplete(databaseError, null);
                }
            }
        });
    }
}
