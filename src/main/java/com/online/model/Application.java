package com.capgemini.model;

import java.util.Date;

public class Application {

    private String applicationNumber;
    private Date applicationDate;
    private String modeOfPayment;
    private double amountPaid;
    private String paymentStatus;
    private String remarks;

    private ApplicationType type;
    private ApplicationStatus status;
    private Applicant applicant;

    public Application() {
    }

    public Application(String applicationNumber, Date applicationDate, String modeOfPayment,
                        double amountPaid, String paymentStatus, String remarks,
                        ApplicationType type, ApplicationStatus status, Applicant applicant) {
        this.applicationNumber = applicationNumber;
        this.applicationDate = applicationDate;
        this.modeOfPayment = modeOfPayment;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
        this.remarks = remarks;
        this.type = type;
        this.status = status;
        this.applicant = applicant;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ApplicationType getType() {
        return type;
    }

    public void setType(ApplicationType type) {
        this.type = type;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
}
