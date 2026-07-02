package com.online.exception;

/**
 * ApplicantNotFoundException - Custom exception thrown when an applicant is not found
 * US-014: Manage Applicant Records
 */
public class ApplicantNotFoundException extends RuntimeException {

    public ApplicantNotFoundException(String message) {
        super(message);
    }

    public ApplicantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicantNotFoundException(Long applicantId) {
        super("Applicant not found with ID: " + applicantId);
    }
}
