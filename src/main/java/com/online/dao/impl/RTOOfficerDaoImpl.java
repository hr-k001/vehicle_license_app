package com.capgemini.dao.impl;

import com.capgemini.dao.RTOOfficerDao;
import com.capgemini.model.Application;
import com.capgemini.model.ApplicationStatus;
import com.capgemini.model.DrivingLicense;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RTOOfficerDaoImpl implements RTOOfficerDao {

    // shares the same in-memory application records created via LicenseDaoImpl
    private final Map<String, Application> applicationStore;
    private final AtomicInteger licenseSequence = new AtomicInteger(5000);

    public RTOOfficerDaoImpl(Map<String, Application> applicationStore) {
        this.applicationStore = applicationStore;
    }

    @Override
    public Application getApplicationById(String applicationNumber) {
        return applicationStore.get(applicationNumber);
    }

    @Override
    public String updateApplicationById(String applicationNumber, Application application) {
        if (!applicationStore.containsKey(applicationNumber)) {
            return "Application not found";
        }
        applicationStore.put(applicationNumber, application);
        return "Status updated to " + application.getStatus();
    }

    @Override
    public DrivingLicense createLearnerLicense(String applicationNumber) {
        Application application = applicationStore.get(applicationNumber);
        if (application == null || application.getStatus() != ApplicationStatus.APPROVED) {
            return null;
        }
        String licenseNumber = "LL-LIC-" + licenseSequence.incrementAndGet();
        Date issueDate = new Date();
        Date validTill = new Date(issueDate.getTime() + (180L * 24 * 60 * 60 * 1000)); // valid 6 months
        return new DrivingLicense(licenseNumber, issueDate, validTill);
    }
}
