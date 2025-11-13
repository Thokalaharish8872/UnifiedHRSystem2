package com.unifiedhr.system.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.unifiedhr.system.R;
import com.unifiedhr.system.adapters.MessageAdapter;
import com.unifiedhr.system.models.Message;
import com.unifiedhr.system.services.MessageService;
import com.unifiedhr.system.utils.FirebaseHelper;
import com.unifiedhr.system.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText etMessage;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnSend;
    private MessageService messageService;
    private String jobId;
    private String applicantId;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        jobId = getIntent().getStringExtra("jobId");
        applicantId = getIntent().getStringExtra("applicantId");

        if (jobId == null || applicantId == null) {
            Toast.makeText(this, "Invalid job or applicant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UnifiedHR", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userName = prefs.getString("userName", "User");
        String userRole = prefs.getString("userRole", "");

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Messages");
        }

        messageService = new MessageService();
        messageList = new ArrayList<>();

        initViews();
        loadMessages();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList, userId);
        recyclerView.setAdapter(adapter);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        messageService.getMessagesByJobAndApplicant(jobId, applicantId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    messageList.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Message message = child.getValue(Message.class);
                        if (message != null && 
                            message.getJobId().equals(jobId) && 
                            message.getApplicantId().equals(applicantId)) {
                            messageList.add(message);
                        }
                    }
                    // Sort by timestamp
                    messageList.sort((m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                    adapter.notifyDataSetChanged();
                    if (messageList.size() > 0) {
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(MessagingActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        String messageId = Utils.generateId();
        String userRole = getSharedPreferences("UnifiedHR", MODE_PRIVATE).getString("userRole", "");
        
        // Set recruiterId if sender is Admin/Manager, otherwise leave empty
        String recruiterId = (userRole.equals("Admin") || userRole.equals("Manager")) ? userId : "";
        
        Message message = new Message(messageId, jobId, applicantId, recruiterId, userId, userName, messageText);

        messageService.sendMessage(message, (error, ref) -> {
            if (error == null) {
                etMessage.setText("");
            } else {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
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

