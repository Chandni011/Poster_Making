package com.deificdigital.poster_making.responses;

import com.google.gson.annotations.SerializedName;

public class TransactionUpdateResponse {
    private String message;
    private int status;
    private Data data;

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String id;
        private String user_id;
        private String user_name;
        private String user_email;
        private String user_mobile;
        private String package_id;
        private String receipt_number;
        private String transcation_id;
        private String order_id;
        private String transcation_status;
        private String amount;
        private String status;
        private String transcation_time;

        // Getters and setters for all fields
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getUser_mobile() {
            return user_mobile;
        }

        public void setUser_mobile(String user_mobile) {
            this.user_mobile = user_mobile;
        }

        public String getPackage_id() {
            return package_id;
        }

        public void setPackage_id(String package_id) {
            this.package_id = package_id;
        }

        public String getReceipt_number() {
            return receipt_number;
        }

        public void setReceipt_number(String receipt_number) {
            this.receipt_number = receipt_number;
        }

        public String getTranscation_id() {
            return transcation_id;
        }

        public void setTranscation_id(String transcation_id) {
            this.transcation_id = transcation_id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getTranscation_status() {
            return transcation_status;
        }

        public void setTranscation_status(String transcation_status) {
            this.transcation_status = transcation_status;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTranscation_time() {
            return transcation_time;
        }

        public void setTranscation_time(String transcation_time) {
            this.transcation_time = transcation_time;
        }
    }
}