package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.FeedbackAdapter;
import com.unifiedhr.system.models.Feedback;
import com.unifiedhr.system.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class EmployeeFeedbackActivity extends AppCompatActivity {

    private RecyclerView rvFeedback;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_feedback);

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");

        rvFeedback = findViewById(R.id.rvFeedback);
        rvFeedback.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        rvFeedback.setAdapter(adapter);

        loadFeedback();
    }

    private void loadFeedback() {
        DatabaseReference ref = FirebaseHelper.getInstance()
                .getDatabaseReference("feedback")
                .child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                feedbackList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Feedback feedback = data.getValue(Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeFeedbackActivity.this,
                        "Failed to load feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
