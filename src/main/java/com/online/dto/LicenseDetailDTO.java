package com.online.dto;

import java.time.LocalDate;

// US-016: View License Details
public class LicenseDetailDTO {

    private String applicantName;
    private String licenseNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String vehicleCategory;
    private String status;

    public LicenseDetailDTO() {}

    public LicenseDetailDTO(String applicantName, String licenseNumber, LocalDate issueDate,
                             LocalDate expiryDate, String vehicleCategory, String status) {
        this.applicantName = applicantName;
        this.licenseNumber = licenseNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.vehicleCategory = vehicleCategory;
        this.status = status;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String applicantName, licenseNumber, vehicleCategory, status;
        private LocalDate issueDate, expiryDate;
        public Builder applicantName(String v) { this.applicantName = v; return this; }
        public Builder licenseNumber(String v) { this.licenseNumber = v; return this; }
        public Builder issueDate(LocalDate v) { this.issueDate = v; return this; }
        public Builder expiryDate(LocalDate v) { this.expiryDate = v; return this; }
        public Builder vehicleCategory(String v) { this.vehicleCategory = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public LicenseDetailDTO build() {
            return new LicenseDetailDTO(applicantName, licenseNumber, issueDate, expiryDate, vehicleCategory, status);
        }
    }

    public String getApplicantName() { return applicantName; }
    public String getLicenseNumber() { return licenseNumber; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getVehicleCategory() { return vehicleCategory; }
    public String getStatus() { return status; }
}
