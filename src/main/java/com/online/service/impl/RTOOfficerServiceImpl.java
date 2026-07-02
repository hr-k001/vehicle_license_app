package com.online.service.impl;

import com.online.dao.RTOOfficerDao;
import com.online.model.Applicant;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.ApplicationType;
import com.online.service.LicenseService;
import com.online.service.RTOOfficerService;

import java.util.List;

public class RTOOfficerServiceImpl implements RTOOfficerService {

    private final RTOOfficerDao rtoOfficerDao;
    private final LicenseService licenseService;

    public RTOOfficerServiceImpl(RTOOfficerDao rtoOfficerDao) {
        this(rtoOfficerDao, null);
    }

    public RTOOfficerServiceImpl(RTOOfficerDao rtoOfficerDao, LicenseService licenseService) {
        this.rtoOfficerDao = rtoOfficerDao;
        this.licenseService = licenseService;
    }

    // US-005 (Mansidak)
    @Override
    public String approveLearnerLicense(String applicationNumber) {
        Application application = rtoOfficerDao.getApplicationById(applicationNumber);
        if (application == null) {
            return "Application not found";
        }
        application.setStatus(ApplicationStatus.APPROVED);
        return rtoOfficerDao.updateApplicationById(applicationNumber, application);
    }

    // US-006 (Himanshu)
    @Override
    public String rejectLearnerLicense(String applicationNumber) {
        Application application = rtoOfficerDao.getApplicationById(applicationNumber);
        if (application == null) {
            return "Application not found";
        }
        application.setStatus(ApplicationStatus.REJECTED);
        rtoOfficerDao.updateApplicationById(applicationNumber, application);
        return "Status updated to Rejected";
    }

    // US-010 (Himanshu) — DL approval requires a PASS test result
    @Override
    public String approveDrivingLicense(String applicationNumber) {
        Application application = rtoOfficerDao.getApplicationById(applicationNumber);
        if (application == null) return "Application not found";
        if (!"PASS".equals(application.getTestResult())) {
            return "Cannot approve: applicant has not passed the driving test";
        }
        application.setStatus(ApplicationStatus.APPROVED);
        rtoOfficerDao.updateApplicationById(applicationNumber, application);
        if (application.getType() == ApplicationType.DL && licenseService != null) {
            licenseService.generateLicenseNumber(applicationNumber);
        }
        return "License approved successfully";
    }

    // US-011 (Himanshu)
    @Override
    public String rejectDrivingLicense(String applicationNumber) {
        Application application = rtoOfficerDao.getApplicationById(applicationNumber);
        if (application == null) return "Application not found";
        application.setStatus(ApplicationStatus.REJECTED);
        rtoOfficerDao.updateApplicationById(applicationNumber, application);
        return "License rejection recorded";
    }

    // US-012 (Himanshu)
    @Override
    public List<Application> getAllApplications() {
        return rtoOfficerDao.getAllApplications();
    }

    // US-013 (Himanshu)
    @Override
    public List<Application> searchApplications(String query) {
        return rtoOfficerDao.searchApplications(query);
    }

    @Override
    public String updateApplicationDetails(String applicationNumber, Application updatedApplication) {
        Application existing = rtoOfficerDao.getApplicationById(applicationNumber);
        if (existing == null) return "Application not found";

        if (updatedApplication.getStatus() != null) {
            existing.setStatus(updatedApplication.getStatus());
        }
        if (updatedApplication.getRemarks() != null) {
            existing.setRemarks(updatedApplication.getRemarks());
        }
        if (updatedApplication.getModeOfPayment() != null) {
            existing.setModeOfPayment(updatedApplication.getModeOfPayment());
        }
        if (updatedApplication.getAmountPaid() != existing.getAmountPaid()) {
            existing.setAmountPaid(updatedApplication.getAmountPaid());
        }
        if (updatedApplication.getPaymentStatus() != null) {
            existing.setPaymentStatus(updatedApplication.getPaymentStatus());
        }
        if (updatedApplication.getTestDate() != null) {
            existing.setTestDate(updatedApplication.getTestDate());
        }
        if (updatedApplication.getTestResult() != null) {
            existing.setTestResult(updatedApplication.getTestResult());
        }

        if (updatedApplication.getApplicant() != null) {
            Applicant applicant = existing.getApplicant();
            if (applicant == null) {
                applicant = new Applicant();
                existing.setApplicant(applicant);
            }
            Applicant updates = updatedApplication.getApplicant();
            if (updates.getFullName() != null) applicant.setFullName(updates.getFullName());
            if (updates.getEmail() != null) applicant.setEmail(updates.getEmail());
            if (updates.getPhone() != null) applicant.setPhone(updates.getPhone());
            if (updates.getAddress() != null) applicant.setAddress(updates.getAddress());
            if (updates.getAadhaarNumber() != null) applicant.setAadhaarNumber(updates.getAadhaarNumber());
            if (updates.getDateOfBirth() != null) applicant.setDateOfBirth(updates.getDateOfBirth());
        }

        rtoOfficerDao.updateApplicationById(applicationNumber, existing);
        return "Application updated successfully";
    }

    @Override
    public String passTest(String applicationNumber) {
        Application application = rtoOfficerDao.getApplicationById(applicationNumber);
        if (application == null) return "Application not found";
        return rtoOfficerDao.updateTestResult(applicationNumber, "PASS");
    }

    @Override
    public String failTest(String applicationNumber) {
        Application application = rtoOfficerDao.getApplicationById(applicationNumber);
        if (application == null) return "Application not found";
        return rtoOfficerDao.updateTestResult(applicationNumber, "FAIL");
    }
}
