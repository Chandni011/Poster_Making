package com.deificdigital.poster_making.responses;

public class TransactionResponse {
    private String message;
    private String receipt_id;
    private int status;

    public String getMessage() {
        return message;
    }

    public String getReceiptId() {
        return receipt_id;
    }

    public int getStatus() {
        return status;
    }
}