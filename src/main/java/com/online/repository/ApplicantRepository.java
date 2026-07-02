package com.online.repository;

import com.online.model.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ApplicantRepository - Data access layer for Applicant entity
 * Uses JpaRepository for CRUD operations
 * US-014: Manage Applicant Records
 */
@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    /**
     * Find applicant by email
     * @param email applicant's email
     * @return Optional containing the applicant if found
     */
    Optional<Applicant> findByEmail(String email);

    /**
     * Find applicant by Aadhaar number
     * @param aadhaarNumber applicant's Aadhaar number
     * @return Optional containing the applicant if found
     */
    Optional<Applicant> findByAadhaarNumber(String aadhaarNumber);

    /**
     * Find applicant by learner license number
     * @param learnerLicenseNumber applicant's learner license number
     * @return Optional containing the applicant if found
     */
    Optional<Applicant> findByLearnerLicenseNumber(String learnerLicenseNumber);

    /**
     * Find applicant by driving license number
     * @param drivingLicenseNumber applicant's driving license number
     * @return Optional containing the applicant if found
     */
    Optional<Applicant> findByDrivingLicenseNumber(String drivingLicenseNumber);
}
