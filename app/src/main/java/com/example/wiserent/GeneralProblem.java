package com.example.wiserent;

public class GeneralProblem extends Appeal{
// class to demonstrate a rented appeal with no type.
    private String content;
    private boolean status;

    public GeneralProblem(String content, boolean status) {
        super("GeneralProblem");
        this.content = content;
        this.status = status;
    }
    public GeneralProblem(){
        super("GeneralProblem");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
