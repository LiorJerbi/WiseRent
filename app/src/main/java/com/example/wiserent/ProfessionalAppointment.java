package com.example.wiserent;

import java.io.Serializable;

public class ProfessionalAppointment extends Appeal implements Serializable {

    private String professionalType;
    private String date;
    private boolean status;

    public ProfessionalAppointment(String professionalType, String date, boolean status) {
        super("Professional Appointment");
        this.professionalType = professionalType;
        this.date = date;
        this.status = status;
    }

    public ProfessionalAppointment() {
        super("Professional Appointment");
    }

    public String getProfessionalType() {
        return professionalType;
    }

    public void setProfessionalType(String professionalType) {
        this.professionalType = professionalType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
