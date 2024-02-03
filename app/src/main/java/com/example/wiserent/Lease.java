package com.example.wiserent;

public class Lease {
    private String leaseId;
    private String propertyId;
    private  String renterId;   // the lessor of the property
    private String rentedId;  //the lessee of the property
    private boolean status;

    public Lease(String leaseId, String propertyId, String renterId, String rentedId,boolean status) {
        this.leaseId = leaseId;
        this.propertyId = propertyId;
        this.renterId = renterId;
        this.rentedId = rentedId;
        this.status = status;
    }

    public String getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}