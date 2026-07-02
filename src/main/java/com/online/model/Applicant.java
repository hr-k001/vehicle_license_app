package com.online.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Applicant Entity
 * US-014: Manage Applicant Records
 */
@Entity
@Table(name = "applicants")
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicantId;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits")
    @Column(nullable = false, unique = true)
    private String aadhaarNumber;

    @NotNull(message = "Date of birth is required")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String learnerLicenseNumber;

    @Column(unique = true)
    private String drivingLicenseNumber;

    public Applicant() {
    }

    public Applicant(Long applicantId, String fullName, String email, String phone, String address,
                     String aadhaarNumber, LocalDate dateOfBirth, String learnerLicenseNumber,
                     String drivingLicenseNumber) {
        this.applicantId = applicantId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.aadhaarNumber = aadhaarNumber;
        this.dateOfBirth = dateOfBirth;
        this.learnerLicenseNumber = learnerLicenseNumber;
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long applicantId;
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private String aadhaarNumber;
        private LocalDate dateOfBirth;
        private String learnerLicenseNumber;
        private String drivingLicenseNumber;

        public Builder applicantId(Long applicantId) { this.applicantId = applicantId; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder aadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; return this; }
        public Builder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public Builder learnerLicenseNumber(String learnerLicenseNumber) { this.learnerLicenseNumber = learnerLicenseNumber; return this; }
        public Builder drivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; return this; }

        public Applicant build() {
            return new Applicant(applicantId, fullName, email, phone, address, aadhaarNumber,
                    dateOfBirth, learnerLicenseNumber, drivingLicenseNumber);
        }
    }

    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getLearnerLicenseNumber() { return learnerLicenseNumber; }
    public void setLearnerLicenseNumber(String learnerLicenseNumber) { this.learnerLicenseNumber = learnerLicenseNumber; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }
}
