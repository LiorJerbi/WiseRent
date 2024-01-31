package com.example.wiserent;

public class PaymentInfo {

    private String month, billType;
    private double amount;

    public PaymentInfo(String month, String billType, double amount) {
        this.month = month;
        this.billType = billType;
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public PaymentInfo() {
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
