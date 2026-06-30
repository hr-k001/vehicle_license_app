package com.capgemini.service.impl;

import com.capgemini.dao.LicenseDao;
import com.capgemini.model.Application;
import com.capgemini.model.ApplicationStatus;
import com.capgemini.model.ApplicationType;
import com.capgemini.service.LicenseService;

import java.util.Date;

public class LicenseServiceImpl implements LicenseService {

    private final LicenseDao licenseDao;

    public LicenseServiceImpl(LicenseDao licenseDao) {
        this.licenseDao = licenseDao;
    }

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

    @Override
    public ApplicationStatus viewLLStatus(String applicationNumber) {
        Application application = licenseDao.getApplicationById(applicationNumber);
        if (application == null) {
            return null;
        }
        return application.getStatus();
    }
}
