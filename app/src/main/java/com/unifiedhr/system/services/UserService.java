package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.unifiedhr.system.models.User;
import com.unifiedhr.system.utils.FirebaseHelper;
import com.unifiedhr.system.utils.Utils;

public class UserService {
    private DatabaseReference usersRef;

    public UserService() {
        usersRef = FirebaseHelper.getInstance().getDatabaseReference("users");
    }

    public void createUser(User user, DatabaseReference.CompletionListener listener) {
        usersRef.child(user.getUserId()).setValue(user, listener);
    }

    public DatabaseReference getUser(String userId) {
        return usersRef.child(userId);
    }

    public DatabaseReference getAllUsers() {
        return usersRef;
    }
}








