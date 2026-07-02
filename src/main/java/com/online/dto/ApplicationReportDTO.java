package com.online.dto;

import java.util.Date;

// US-018: Application Reports
public class ApplicationReportDTO {

    private String applicationNumber;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String type;
    private String status;
    private Date applicationDate;
    private Date testDate;
    private String testResult;
    private double amountPaid;
    private String paymentStatus;

    public ApplicationReportDTO() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String applicationNumber, applicantName, applicantEmail, applicantPhone;
        private String type, status, testResult, paymentStatus;
        private Date applicationDate, testDate;
        private double amountPaid;

        public Builder applicationNumber(String v) { this.applicationNumber = v; return this; }
        public Builder applicantName(String v) { this.applicantName = v; return this; }
        public Builder applicantEmail(String v) { this.applicantEmail = v; return this; }
        public Builder applicantPhone(String v) { this.applicantPhone = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder applicationDate(Date v) { this.applicationDate = v; return this; }
        public Builder testDate(Date v) { this.testDate = v; return this; }
        public Builder testResult(String v) { this.testResult = v; return this; }
        public Builder amountPaid(double v) { this.amountPaid = v; return this; }
        public Builder paymentStatus(String v) { this.paymentStatus = v; return this; }

        public ApplicationReportDTO build() {
            ApplicationReportDTO d = new ApplicationReportDTO();
            d.applicationNumber = applicationNumber;
            d.applicantName = applicantName;
            d.applicantEmail = applicantEmail;
            d.applicantPhone = applicantPhone;
            d.type = type;
            d.status = status;
            d.applicationDate = applicationDate;
            d.testDate = testDate;
            d.testResult = testResult;
            d.amountPaid = amountPaid;
            d.paymentStatus = paymentStatus;
            return d;
        }
    }

    public String getApplicationNumber() { return applicationNumber; }
    public String getApplicantName() { return applicantName; }
    public String getApplicantEmail() { return applicantEmail; }
    public String getApplicantPhone() { return applicantPhone; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Date getApplicationDate() { return applicationDate; }
    public Date getTestDate() { return testDate; }
    public String getTestResult() { return testResult; }
    public double getAmountPaid() { return amountPaid; }
    public String getPaymentStatus() { return paymentStatus; }
}
