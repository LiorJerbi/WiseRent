package com.example.wiserent;

public class Lease {
    private String leaseId;
    private Property property;
    private  String renterId;   // the lessor of the property
    private String rentedId;  //the lessee of the property

    public Lease(String leaseId, Property property, String renterId, String rentedId) {
        this.leaseId = leaseId;
        this.property = property;
        this.renterId = renterId;
        this.rentedId = rentedId;
    }

    public String getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
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
}
