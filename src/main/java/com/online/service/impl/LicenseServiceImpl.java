package com.online.service.impl;

import com.online.dao.LicenseDao;
import com.online.dto.LicenseDetailDTO;
import com.online.model.*;
import com.online.repository.DrivingLicenseRepository;
import com.online.repository.LicenseRepository;
import com.online.repository.ApplicantRepository;
import com.online.util.LicenseNumberGenerator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;

public class LicenseServiceImpl implements com.online.service.LicenseService {

    private final LicenseDao licenseDao;
    private final LicenseRepository licenseRepository;
    private final ApplicantRepository applicantRepository;
    private final LicenseNumberGenerator licenseNumberGenerator;

    public LicenseServiceImpl(LicenseDao licenseDao) {
        this(licenseDao, (LicenseRepository) null);
    }

    public LicenseServiceImpl(LicenseDao licenseDao, LicenseRepository licenseRepository) {
        this(licenseDao, licenseRepository, null);
    }

    public LicenseServiceImpl(LicenseDao licenseDao, LicenseRepository licenseRepository, ApplicantRepository applicantRepository) {
        this.licenseDao = licenseDao;
        this.licenseRepository = licenseRepository;
        this.applicantRepository = applicantRepository;
        this.licenseNumberGenerator = new LicenseNumberGenerator();
    }

    public LicenseServiceImpl(LicenseDao licenseDao, DrivingLicenseRepository drivingLicenseRepository) {
        this(licenseDao, (LicenseRepository) drivingLicenseRepository, null);
    }

    // US-003
    @Override
    public String applyForLL(Application application) {
        if (application == null) return "Invalid application details";
        application.setType(ApplicationType.LL);
        if (application.getApplicationDate() == null) application.setApplicationDate(new Date());
        return licenseDao.createLLRequest(application);
    }

    // US-004
    @Override
    public ApplicationStatus viewLLStatus(String applicationNumber) {
        Application app = licenseDao.getApplicationById(applicationNumber);
        return app == null ? null : app.getStatus();
    }

    // US-007
    @Override
    public String applyForDL(Application application) {
        if (application == null) return "Invalid application details";
        application.setType(ApplicationType.DL);
        if (application.getApplicationDate() == null) application.setApplicationDate(new Date());
        return licenseDao.createDLRequest(application);
    }

    // US-008
    @Override
    public String scheduleDrivingTest(String applicationNumber, Date testDate) {
        if (applicationNumber == null || testDate == null) return "Invalid scheduling details";
        return licenseDao.scheduleTest(applicationNumber, testDate);
    }

    // US-009
    @Override
    public ApplicationStatus viewDLStatus(String applicationNumber) {
        Application app = licenseDao.getApplicationById(applicationNumber);
        if (app == null || app.getType() != ApplicationType.DL) return null;
        return app.getStatus();
    }

    @Override
    public Application getApplicationById(String applicationNumber) {
        return licenseDao.getApplicationById(applicationNumber);
    }

    @Override
    public Application getLLApplicationByEmail(String email) {
        return licenseDao.getLLApplicationByEmail(email);
    }

    // US-015
    @Override
    public String generateLicenseNumber(String applicationId) {
        Application app = licenseDao.getApplicationById(applicationId);
        if (app == null) return "Application not found with ID: " + applicationId;
        if (app.getType() != ApplicationType.DL)
            return "Invalid application type. Only DL applications can have license numbers generated";
        if (app.getStatus() != ApplicationStatus.APPROVED)
            return "Application must be approved before generating license number";
        if (app.getApplicant() != null && app.getApplicant().getDrivingLicenseNumber() != null) {
            return "License generated successfully: " + app.getApplicant().getDrivingLicenseNumber();
        }

        String licenseNumber = generateUniqueLicenseNumber();
        if (app.getApplicant() != null) {
            app.getApplicant().setDrivingLicenseNumber(licenseNumber);
        }

        if (licenseRepository != null) {
            Applicant applicant = app.getApplicant();
            if (applicant != null && applicantRepository != null && applicant.getApplicantId() == null) {
                java.util.Optional<Applicant> existingApplicant = java.util.Optional.empty();

                if (applicant.getAadhaarNumber() != null) {
                    existingApplicant = applicantRepository.findByAadhaarNumber(applicant.getAadhaarNumber());
                }
                if (existingApplicant.isEmpty() && applicant.getEmail() != null) {
                    existingApplicant = applicantRepository.findByEmail(applicant.getEmail());
                }

                if (existingApplicant.isPresent()) {
                    applicant = existingApplicant.get();
                    app.setApplicant(applicant);
                } else {
                    applicant = applicantRepository.save(applicant);
                    app.setApplicant(applicant);
                }
                applicant.setDrivingLicenseNumber(licenseNumber);
                applicantRepository.save(applicant);
            }

            DrivingLicense drivingLicense = DrivingLicense.builder()
                    .drivingLicenseNumber(licenseNumber)
                    .dateOfIssue(LocalDate.now())
                    .validTill(LocalDate.now().plusYears(10))
                    .applicant(app.getApplicant())
                    .issueAuthority("RTO")
                    .remarks("Generated automatically")
                    .build();
            licenseRepository.save(drivingLicense);
        }

        return "License generated successfully: " + licenseNumber;
    }

    private String generateUniqueLicenseNumber() {
        if (licenseRepository == null) {
            return licenseNumberGenerator.generateLicenseNumber();
        }

        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = licenseNumberGenerator.generateLicenseNumber();
            if (licenseRepository.findByDrivingLicenseNumber(candidate).isEmpty()) {
                return candidate;
            }
        }

        throw new IllegalStateException("Unable to generate a unique license number");
    }

    // US-016
    @Override
    public LicenseDetailDTO viewLicenseDetails(String licenseNumber) {
        if (licenseRepository == null || licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return null;
        }

        return licenseRepository.findByDrivingLicenseNumber(licenseNumber.trim())
                .map(license -> LicenseDetailDTO.builder()
                        .applicantName(license.getApplicant() != null ? license.getApplicant().getFullName() : "N/A")
                        .licenseNumber(license.getDrivingLicenseNumber())
                        .issueDate(license.getDateOfIssue())
                        .expiryDate(license.getValidTill())
                        .vehicleCategory("LMV")
                        .status("ISSUED")
                        .build())
                .orElse(null);
    }

    @Override
    public Map<String, Boolean> getApplicantProgress(String email) {
        Map<String, Boolean> progress = new HashMap<>();
        progress.put("registered", email != null && !email.trim().isEmpty());
        progress.put("llApplied", false);
        progress.put("llApproved", false);
        progress.put("dlApplied", false);
        progress.put("testBooked", false);
        progress.put("dlIssued", false);

        if (email == null || email.trim().isEmpty()) {
            return progress;
        }

        String lower = email.trim().toLowerCase();
        for (Application app : licenseDao.getApplicationStore().values()) {
            if (app.getApplicant() == null || app.getApplicant().getEmail() == null) continue;
            if (!lower.equals(app.getApplicant().getEmail().trim().toLowerCase())) continue;

            if (app.getType() == ApplicationType.LL) {
                progress.put("llApplied", true);
                if (app.getStatus() == ApplicationStatus.APPROVED) {
                    progress.put("llApproved", true);
                }
            }

            if (app.getType() == ApplicationType.DL) {
                progress.put("dlApplied", true);
                if (app.getTestDate() != null) {
                    progress.put("testBooked", true);
                }
                if (app.getApplicant().getDrivingLicenseNumber() != null) {
                    progress.put("dlIssued", true);
                }
            }
        }

        return progress;
    }
}
