package com.example.wiserent;

public class Appeal {

    private String appealId;
    private String type; // Bill/Professional


    public Appeal(String type) {
        this.type = type;
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
}
