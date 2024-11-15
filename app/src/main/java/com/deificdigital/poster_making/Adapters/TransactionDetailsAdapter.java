package com.deificdigital.poster_making.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.TransactionDetails;

import java.util.List;

public class TransactionDetailsAdapter extends RecyclerView.Adapter<TransactionDetailsAdapter.ViewHolder> {
    private List<TransactionDetails> transactions;

    public TransactionDetailsAdapter(List<TransactionDetails> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionDetails transaction = transactions.get(position);
        holder.tvTransactionId.setText(transaction.getTransactionId());
        holder.tvAmount.setText(transaction.getAmount());
        holder.tvTransactionTime.setText(transaction.getTransactionTime());
        holder.tvPackageName.setText(transaction.getPackageName());
        holder.tvReceiptNumber.setText(transaction.getReceiptNumber());

        if ("1".equals(transaction.getTransactionStatus())) {
            holder.tvTransactionStatus.setText("Success");
            holder.tvTransactionStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else if ("0".equals(transaction.getTransactionStatus())) {
            holder.tvTransactionStatus.setText("Failed");
            holder.tvTransactionStatus.setTextColor(Color.RED);
        } else {
            holder.tvTransactionStatus.setText(" ");
            holder.tvTransactionStatus.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId, tvAmount, tvTransactionTime, tvTransactionStatus, tvPackageName, tvReceiptNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId = itemView.findViewById(R.id.transactionId);
            tvAmount = itemView.findViewById(R.id.amount);
            tvTransactionTime = itemView.findViewById(R.id.transactionTime);
            tvTransactionStatus = itemView.findViewById(R.id.status);
            tvPackageName = itemView.findViewById(R.id.packageName);
            tvReceiptNumber = itemView.findViewById(R.id.receiptNumber);
        }
    }
}
