package com.example.wiserent;

public class Bill extends Appeal{
//class to demonstrate Bill object extends Appeal
    private String month;
    private String billType;
    private double amount;
    private boolean status;


    public Bill(String month, String billType, double amount) {
        super("Bill");
        this.month = month;
        this.billType = billType;
        this.amount = amount;
    }
    public Bill() {
        super("Bill");
    }
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
