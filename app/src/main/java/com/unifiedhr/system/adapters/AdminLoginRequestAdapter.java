package com.unifiedhr.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.AdminLoginRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminLoginRequestAdapter extends RecyclerView.Adapter<AdminLoginRequestAdapter.RequestViewHolder> {
    private List<AdminLoginRequest> requestList;
    private RequestActionListener listener;

    public interface RequestActionListener {
        void onApprove(AdminLoginRequest request);
        void onReject(AdminLoginRequest request);
    }

    public AdminLoginRequestAdapter(List<AdminLoginRequest> requestList, RequestActionListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_login_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        AdminLoginRequest request = requestList.get(position);
        holder.tvName.setText(request.getName());
        holder.tvEmail.setText(request.getEmail());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(request.getRequestedAt()));
        holder.tvRequestDate.setText("Requested: " + dateStr);

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(request);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName, tvEmail, tvRequestDate;
        Button btnApprove, btnReject;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}