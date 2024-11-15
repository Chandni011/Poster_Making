package com.deificdigital.poster_making.models;

import com.google.gson.annotations.SerializedName;

public class TransactionDetails {
    @SerializedName("transcation_id")
    private String transactionId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_mobile")
    private String userMobile;

    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("package_name")
    private String packageName;

    @SerializedName("transcation_status")
    private String transactionStatus;

    @SerializedName("amount")
    private String amount;

    @SerializedName("receipt_number")
    private String receiptNumber;

    @SerializedName("transcation_time")
    private String transactionTime;

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }
}
