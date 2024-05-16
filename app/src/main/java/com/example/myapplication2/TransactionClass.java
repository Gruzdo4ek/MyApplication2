package com.example.myapplication2;

public class TransactionClass {
    private int amount;
    private String message;
    private boolean positive;
    private String category;
    public TransactionClass(int amount, String message, boolean positive, String category) {
        this.amount = amount;
        this.message = message;
        this.positive = positive;
        this.category = category;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isPositive() {
        return positive;
    }
    public void setPositive(boolean positive) {
        this.positive = positive;
    }
    public String getCategory() {
        return category;
    }
}