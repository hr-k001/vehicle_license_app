package com.online.service.impl;

import com.online.exception.ApplicantNotFoundException;
import com.online.model.Applicant;
import com.online.repository.ApplicantRepository;
import com.online.service.ApplicantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ApplicantServiceImpl
 * US-014: Manage Applicant Records
 */
@Service
@Transactional
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;

    public ApplicantServiceImpl(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Applicant> getAllApplicants() {
        return applicantRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Applicant getApplicantById(Long applicantId) {
        return applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ApplicantNotFoundException(applicantId));
    }

    @Override
    public Applicant createApplicant(Applicant applicant) {
        ensureUniqueApplicant(applicant, null);
        return applicantRepository.save(applicant);
    }

    @Override
    public Applicant updateApplicant(Long applicantId, Applicant details) {
        Applicant existing = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ApplicantNotFoundException(applicantId));
        ensureUniqueApplicant(details, applicantId);
        existing.setFullName(details.getFullName());
        existing.setEmail(details.getEmail());
        existing.setPhone(details.getPhone());
        existing.setAddress(details.getAddress());
        existing.setAadhaarNumber(details.getAadhaarNumber());
        existing.setDateOfBirth(details.getDateOfBirth());
        existing.setVehicleType(details.getVehicleType());
        existing.setLearnerLicenseNumber(details.getLearnerLicenseNumber());
        existing.setDrivingLicenseNumber(details.getDrivingLicenseNumber());
        return applicantRepository.save(existing);
    }

    @Override
    public void deleteApplicant(Long applicantId) {
        if (!applicantRepository.existsById(applicantId)) {
            throw new ApplicantNotFoundException(applicantId);
        }
        applicantRepository.deleteById(applicantId);
    }

    private void ensureUniqueApplicant(Applicant applicant, Long currentApplicantId) {
        if (applicant == null) return;

        findByEmail(applicant.getEmail()).ifPresent(existing -> {
            if (!sameApplicant(existing, currentApplicantId)) {
                throw new IllegalArgumentException("An applicant with this email already exists. Use Edit to update the existing record.");
            }
        });

        findByAadhaar(applicant.getAadhaarNumber()).ifPresent(existing -> {
            if (!sameApplicant(existing, currentApplicantId)) {
                throw new IllegalArgumentException("An applicant with this Aadhaar number already exists. Use Edit to update the existing record.");
            }
        });
    }

    private Optional<Applicant> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return Optional.empty();
        Optional<Applicant> result = applicantRepository.findByEmail(email.trim());
        return result == null ? Optional.empty() : result;
    }

    private Optional<Applicant> findByAadhaar(String aadhaarNumber) {
        if (aadhaarNumber == null || aadhaarNumber.trim().isEmpty()) return Optional.empty();
        Optional<Applicant> result = applicantRepository.findByAadhaarNumber(aadhaarNumber.trim());
        return result == null ? Optional.empty() : result;
    }

    private boolean sameApplicant(Applicant existing, Long currentApplicantId) {
        return currentApplicantId != null && currentApplicantId.equals(existing.getApplicantId());
    }
}
