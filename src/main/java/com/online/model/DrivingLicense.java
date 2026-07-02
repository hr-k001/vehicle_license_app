package com.online.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DrivingLicense Entity
 * US-015: Generate Unique License Number
 */
@Entity
@Table(name = "driving_licenses")
public class DrivingLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long licenseId;

    @NotBlank(message = "License number is required")
    @Column(nullable = false, unique = true)
    private String drivingLicenseNumber;

    @NotNull(message = "Date of issue is required")
    @Column(nullable = false)
    private LocalDate dateOfIssue;

    @NotNull(message = "Valid till date is required")
    @Column(nullable = false)
    private LocalDate validTill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @Column
    private String issueAuthority;

    @Column
    private String remarks;

    public DrivingLicense() {
    }

    public DrivingLicense(Long licenseId, String drivingLicenseNumber, LocalDate dateOfIssue,
                          LocalDate validTill, Applicant applicant, String issueAuthority, String remarks) {
        this.licenseId = licenseId;
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.dateOfIssue = dateOfIssue;
        this.validTill = validTill;
        this.applicant = applicant;
        this.issueAuthority = issueAuthority;
        this.remarks = remarks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long licenseId;
        private String drivingLicenseNumber;
        private LocalDate dateOfIssue;
        private LocalDate validTill;
        private Applicant applicant;
        private String issueAuthority;
        private String remarks;

        public Builder licenseId(Long licenseId) { this.licenseId = licenseId; return this; }
        public Builder drivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; return this; }
        public Builder dateOfIssue(LocalDate dateOfIssue) { this.dateOfIssue = dateOfIssue; return this; }
        public Builder validTill(LocalDate validTill) { this.validTill = validTill; return this; }
        public Builder applicant(Applicant applicant) { this.applicant = applicant; return this; }
        public Builder issueAuthority(String issueAuthority) { this.issueAuthority = issueAuthority; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }

        public DrivingLicense build() {
            return new DrivingLicense(licenseId, drivingLicenseNumber, dateOfIssue, validTill,
                    applicant, issueAuthority, remarks);
        }
    }

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }
    public LocalDate getDateOfIssue() { return dateOfIssue; }
    public void setDateOfIssue(LocalDate dateOfIssue) { this.dateOfIssue = dateOfIssue; }
    public LocalDate getValidTill() { return validTill; }
    public void setValidTill(LocalDate validTill) { this.validTill = validTill; }
    public Applicant getApplicant() { return applicant; }
    public void setApplicant(Applicant applicant) { this.applicant = applicant; }
    public String getIssueAuthority() { return issueAuthority; }
    public void setIssueAuthority(String issueAuthority) { this.issueAuthority = issueAuthority; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
