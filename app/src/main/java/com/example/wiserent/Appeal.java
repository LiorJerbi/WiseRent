package com.example.wiserent;

import java.io.Serializable;

public class Appeal implements Serializable {

    private String appealId;
    private String type; // Bill/Professional
    private String propertyID;


    public Appeal(String type) {
        this.type = type;
    }
    public Appeal(){

    }

    public String getAppealId() {
        return appealId;
    }

    public void setAppealId(String appealId) {
        this.appealId = appealId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }
}
