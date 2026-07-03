package com.online.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;

import java.util.Date;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    private String applicationNumber;
    private Date applicationDate;
    private String modeOfPayment;
    private double amountPaid;
    private String paymentStatus;
    private String remarks;

    @Enumerated(EnumType.STRING)
    private ApplicationType type;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    @Valid
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Applicant applicant;
    private Date testDate;
    private String testResult; // "PASS" or "FAIL" after driving test

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

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }
}
