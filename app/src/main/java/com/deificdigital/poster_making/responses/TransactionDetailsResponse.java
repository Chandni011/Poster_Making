package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.TransactionDetails;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionDetailsResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("transcation_data")
    private List<TransactionDetails> transactionData;

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<TransactionDetails> getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(List<TransactionDetails> transactionData) {
        this.transactionData = transactionData;
    }
}
