package com.example.wiserent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private List<Property> ownedProperties;
    private List<Lease> leasedProperties;
    private List<Appeal> appeals;

    public User(String userId, String fullName, String email, String phone) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.ownedProperties = new ArrayList<>();
        this.leasedProperties = new ArrayList<>();
        this.appeals = new ArrayList<>();
    }
    public User(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public List<Property> getOwnedProperties() {
        return ownedProperties;
    }

    public void setOwnedProperties(List<Property> ownedProperties) {
        this.ownedProperties = ownedProperties;
    }

    public List<Lease> getLeasedProperties() {
        return leasedProperties;
    }

    public void setLeasedProperties(List<Lease> leasedProperties) {
        this.leasedProperties = leasedProperties;
    }

    // Method to add a property to the list of owned properties
    public void addOwnedProperty(Property property) {
        ownedProperties.add(property);
    }

    // Method to remove an owned property based on its propertyId
    public void removeOwnedProperty(String propertyId) {
        ownedProperties.removeIf(property -> property.getPropertyId().equals(propertyId));
    }

    // Method to edit details of an owned property based on its propertyId
    public void editOwnedProperty(String propertyId, String newAddress, double newRentAmount) {
        for (Property property : ownedProperties) {
            if (property.getPropertyId().equals(propertyId)) {
                property.setAddress(newAddress);
                property.setRentAmount(newRentAmount);
                break;
            }
        }
    }

    // Method to add a leased property to the list of leased properties
    public void addLeasedProperty(Lease lease) {
        leasedProperties.add(lease);
    }

    // Method to remove a leased property based on its propertyId
    public void removeLeasedProperty(String propertyId) {
        leasedProperties.removeIf(lease -> lease.getPropertyId().equals(propertyId));
    }

    // Method to edit details of a leased property based on its propertyId
//    public void editLeasedProperty(String propertyId, String newAddress, double newRentAmount) {
//        for (Lease lease : leasedProperties) {
//            if (lease.getProperty().getPropertyId().equals(propertyId)) {
//                lease.getProperty().setAddress(newAddress);
//                lease.getProperty().setRentAmount(newRentAmount);
//                break;
//            }
//        }
//    }
    public List<Appeal> getAppeals() {
        return appeals;
    }

    public void setAppeals(List<Appeal> appeals) {
        this.appeals = appeals;
    }

    // Method to add an appeal to the list of appeals
    public void addAppeal(Appeal appeal) {
        appeals.add(appeal);
    }

    // Method to remove an appeal based on its appealId
    public void removeAppeal(String appealId) {
        appeals.removeIf(appeal -> appeal.getAppealId().equals(appealId));
    }
}
