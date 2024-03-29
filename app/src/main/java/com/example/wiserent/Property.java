package com.example.wiserent;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Property implements Serializable {
//Class to demonstrate property object
    private String propertyId;
    private String address;
    private double rentAmount;
    private String renterId;
    private String rentedId;

    public Property(String propertyId, String address, double rentAmount, String renterId) {
        this.propertyId = propertyId;
        this.address = address;
        this.rentAmount = rentAmount;
        this.renterId = renterId;
        this.rentedId = null;
    }
    public Property(){}

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public String getRentedId() {
        return rentedId;
    }

    public void setRentedId(String rentedId) {
        this.rentedId = rentedId;
    }

    @NonNull
    @Override
    public String toString() {
        return address;
    }
}
