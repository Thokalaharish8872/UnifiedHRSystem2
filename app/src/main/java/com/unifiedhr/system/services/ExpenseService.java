package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.Expense;
import com.unifiedhr.system.utils.FirebaseHelper;

public class ExpenseService {
    private DatabaseReference expenseRef;

    public ExpenseService() {
        expenseRef = FirebaseHelper.getInstance().getDatabaseReference("expenses");
    }

    public void createExpense(Expense expense, DatabaseReference.CompletionListener listener) {
        expenseRef.child(expense.getExpenseId()).setValue(expense, listener);
    }
}

