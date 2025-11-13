package com.unifiedhr.system.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.unifiedhr.system.R;

public class ApplyJobDialogFragment extends DialogFragment {
    private static final int PICK_PDF = 1;
    private EditText etPhone;
    private Button btnUploadResume, btnApply;
    private TextView tvResumeStatus;
    private Uri resumeUri;
    private OnApplyListener listener;

    public interface OnApplyListener {
        void onApply(String phone, Uri resumeUri);
    }

    public void setOnApplyListener(OnApplyListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_apply_job, container, false);

        etPhone = view.findViewById(R.id.etPhone);
        btnUploadResume = view.findViewById(R.id.btnUploadResume);
        btnApply = view.findViewById(R.id.btnApply);
        tvResumeStatus = view.findViewById(R.id.tvResumeStatus);

        btnUploadResume.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if (getActivity() != null) {
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF);
            }
        });

        btnApply.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (resumeUri == null) {
                Toast.makeText(getContext(), "Please upload your resume", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) {
                listener.onApply(phone, resumeUri);
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF && resultCode == android.app.Activity.RESULT_OK && data != null) {
            resumeUri = data.getData();
            if (resumeUri != null) {
                tvResumeStatus.setText("Resume selected");
                tvResumeStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                btnUploadResume.setText("Change Resume");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.white);
        }
    }
}
