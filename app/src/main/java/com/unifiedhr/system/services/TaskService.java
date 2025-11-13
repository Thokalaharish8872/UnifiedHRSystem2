package com.unifiedhr.system.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.unifiedhr.system.models.Task;
import com.unifiedhr.system.utils.FirebaseHelper;

public class TaskService {
    private DatabaseReference tasksRef;

    public TaskService() {
        tasksRef = FirebaseHelper.getInstance().getDatabaseReference("tasks");
    }

    public void createTask(Task task, DatabaseReference.CompletionListener listener) {
        tasksRef.child(task.getTaskId()).setValue(task, listener);
    }

    public void updateTask(String taskId, Task task, DatabaseReference.CompletionListener listener) {
        tasksRef.child(taskId).setValue(task, listener);
    }

    public DatabaseReference getTask(String taskId) {
        return tasksRef.child(taskId);
    }

    public Query getTasksByEmployee(String employeeId) {
        return tasksRef.orderByChild("assignedTo").equalTo(employeeId);
    }

    public Query getTasksByManager(String managerId) {
        return tasksRef.orderByChild("assignedBy").equalTo(managerId);
    }

    public void deleteTask(String taskId, DatabaseReference.CompletionListener listener) {
        tasksRef.child(taskId).removeValue(listener);
    }
}

