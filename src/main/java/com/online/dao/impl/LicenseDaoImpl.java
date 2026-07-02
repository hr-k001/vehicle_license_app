package com.online.dao.impl;

import com.online.dao.LicenseDao;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.ApplicationType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LicenseDaoImpl implements LicenseDao {

    private final Map<String, Application> applicationStore;
    private final AtomicInteger llSequence = new AtomicInteger(1000);
    private final AtomicInteger dlSequence = new AtomicInteger(2000);

    public LicenseDaoImpl() {
        this.applicationStore = new HashMap<>();
    }

    public LicenseDaoImpl(Map<String, Application> applicationStore) {
        this.applicationStore = applicationStore;
    }

    @Override
    public Map<String, Application> getApplicationStore() {
        return applicationStore;
    }

    // US-003
    @Override
    public String createLLRequest(Application application) {
        if (application == null || application.getApplicant() == null) return "Invalid application details";
        String appNo = "LL-" + llSequence.incrementAndGet();
        application.setApplicationNumber(appNo);
        application.setStatus(ApplicationStatus.PENDING);
        applicationStore.put(appNo, application);
        return "Application saved successfully";
    }

    // US-004
    @Override
    public Application getApplicationById(String applicationNumber) {
        return applicationStore.get(applicationNumber);
    }

    // US-007
    @Override
    public String createDLRequest(Application application) {
        if (application == null || application.getApplicant() == null) return "Invalid application details";
        String appNo = "DL-" + dlSequence.incrementAndGet();
        application.setApplicationNumber(appNo);
        application.setType(ApplicationType.DL);
        application.setStatus(ApplicationStatus.PENDING);
        applicationStore.put(appNo, application);
        return "Application submitted successfully";
    }

    // US-008
    @Override
    public String scheduleTest(String applicationNumber, Date testDate) {
        Application application = applicationStore.get(applicationNumber);
        if (application == null) return "Application not found";
        if (application.getType() != ApplicationType.DL) return "Test scheduling is only for Driving License applications";
        application.setTestDate(testDate);
        applicationStore.put(applicationNumber, application);
        return "Test slot booked successfully";
    }

    @Override
    public Application getLLApplicationByEmail(String email) {
        if (email == null) return null;
        String lower = email.trim().toLowerCase();
        Optional<Application> approved = applicationStore.values().stream()
                .filter(a -> a.getType() == ApplicationType.LL
                        && a.getApplicant() != null
                        && lower.equals(a.getApplicant().getEmail() != null ? a.getApplicant().getEmail().toLowerCase() : "")
                        && a.getStatus() == ApplicationStatus.APPROVED)
                .findFirst();
        if (approved.isPresent()) return approved.get();
        return applicationStore.values().stream()
                .filter(a -> a.getType() == ApplicationType.LL
                        && a.getApplicant() != null
                        && lower.equals(a.getApplicant().getEmail() != null ? a.getApplicant().getEmail().toLowerCase() : ""))
                .findFirst().orElse(null);
    }
}
