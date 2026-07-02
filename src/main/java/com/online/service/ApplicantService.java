package com.online.service;

import com.online.model.Applicant;
import java.util.List;

/**
 * ApplicantService - Interface for Applicant business logic
 * US-014: Manage Applicant Records
 */
public interface ApplicantService {

    /**
     * Get all applicants
     * @return list of all applicants
     */
    List<Applicant> getAllApplicants();

    /**
     * Get applicant by ID
     * @param applicantId applicant's ID
     * @return applicant if found
     * @throws com.online.exception.ApplicantNotFoundException if applicant not found
     */
    Applicant getApplicantById(Long applicantId);

    /**
     * Create a new applicant
     * @param applicant applicant object to be created
     * @return created applicant
     */
    Applicant createApplicant(Applicant applicant);

    /**
     * Update an existing applicant
     * @param applicantId applicant's ID
     * @param applicantDetails updated applicant details
     * @return updated applicant
     * @throws com.online.exception.ApplicantNotFoundException if applicant not found
     */
    Applicant updateApplicant(Long applicantId, Applicant applicantDetails);

    /**
     * Delete an applicant
     * @param applicantId applicant's ID
     * @throws com.online.exception.ApplicantNotFoundException if applicant not found
     */
    void deleteApplicant(Long applicantId);
}
