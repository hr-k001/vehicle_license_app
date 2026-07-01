package com.online.service.impl;

import com.online.dao.RTOOfficerDao;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.service.RTOOfficerService;

import java.util.List;

public class RTOOfficerServiceImpl implements RTOOfficerService {

    private final RTOOfficerDao rtoOfficerDao;

    public RTOOfficerServiceImpl(RTOOfficerDao rtoOfficerDao) {
        this.rtoOfficerDao = rtoOfficerDao;
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
