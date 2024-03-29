package com.example.wiserent;

import java.io.Serializable;

public class Appeal implements Serializable {

    private String appealId;
    private String type; // Bill/Professional/GeneralProblem
    private String propertyId;


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

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
}
