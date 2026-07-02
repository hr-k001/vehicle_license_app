package com.online.service.impl;

import com.online.exception.ApplicantNotFoundException;
import com.online.model.Applicant;
import com.online.repository.ApplicantRepository;
import com.online.service.ApplicantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return applicantRepository.save(applicant);
    }

    @Override
    public Applicant updateApplicant(Long applicantId, Applicant details) {
        Applicant existing = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ApplicantNotFoundException(applicantId));
        existing.setFullName(details.getFullName());
        existing.setEmail(details.getEmail());
        existing.setPhone(details.getPhone());
        existing.setAddress(details.getAddress());
        existing.setAadhaarNumber(details.getAadhaarNumber());
        existing.setDateOfBirth(details.getDateOfBirth());
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
}
