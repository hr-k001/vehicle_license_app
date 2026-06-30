package com.capgemini.service.impl;

import com.capgemini.dao.RTOOfficerDao;
import com.capgemini.model.Application;
import com.capgemini.model.ApplicationStatus;
import com.capgemini.service.RTOOfficerService;

public class RTOOfficerServiceImpl implements RTOOfficerService {

    private final RTOOfficerDao rtoOfficerDao;

    public RTOOfficerServiceImpl(RTOOfficerDao rtoOfficerDao) {
        this.rtoOfficerDao = rtoOfficerDao;
    }

    @Override
    public String approveLearnerLicense(String applicationNumber) {
        Application application = rtoOfficerDao.getApplicationById(applicationNumber);
        if (application == null) {
            return "Application not found";
        }
        application.setStatus(ApplicationStatus.APPROVED);
        return rtoOfficerDao.updateApplicationById(applicationNumber, application);
    }
}
