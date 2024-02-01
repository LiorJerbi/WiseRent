package com.example.wiserent;

public class ProfessionalAppointment extends Appeal{

    private String professionalType;
    private String date;
    private String status;

    public ProfessionalAppointment(String professionalType, String date, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
