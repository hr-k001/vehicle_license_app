package com.online.service.impl;

import com.online.dao.LicenseDao;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.ApplicationType;
import com.online.service.LicenseService;

import java.util.Date;

public class LicenseServiceImpl implements LicenseService {

    private final LicenseDao licenseDao;

    public LicenseServiceImpl(LicenseDao licenseDao) {
        this.licenseDao = licenseDao;
    }

    // US-003 (Mansidak)
    @Override
    public String applyForLL(Application application) {
        if (application == null) {
            return "Invalid application details";
        }
        application.setType(ApplicationType.LL);
        if (application.getApplicationDate() == null) {
            application.setApplicationDate(new Date());
        }
        return licenseDao.createLLRequest(application);
    }

    // US-004 (Mansidak)
    @Override
    public ApplicationStatus viewLLStatus(String applicationNumber) {
        Application application = licenseDao.getApplicationById(applicationNumber);
        if (application == null) {
            return null;
        }
        return application.getStatus();
    }

    // US-007 (Himanshu)
    @Override
    public String applyForDL(Application application) {
        if (application == null) {
            return "Invalid application details";
        }
        application.setType(ApplicationType.DL);
        if (application.getApplicationDate() == null) {
            application.setApplicationDate(new Date());
        }
        return licenseDao.createDLRequest(application);
    }

    // US-008 (Himanshu)
    @Override
    public String scheduleDrivingTest(String applicationNumber, Date testDate) {
        if (applicationNumber == null || testDate == null) {
            return "Invalid scheduling details";
        }
        return licenseDao.scheduleTest(applicationNumber, testDate);
    }

    // US-009 (Himanshu)
    @Override
    public ApplicationStatus viewDLStatus(String applicationNumber) {
        Application application = licenseDao.getApplicationById(applicationNumber);
        if (application == null || application.getType() != ApplicationType.DL) {
            return null;
        }
        return application.getStatus();
    }

    @Override
    public Application getLLApplicationByEmail(String email) {
        return licenseDao.getLLApplicationByEmail(email);
    }
}
