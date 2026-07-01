package com.online.dao.impl;

import com.online.dao.LicenseDao;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.ApplicationType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Comparator;
import java.util.Optional;

public class LicenseDaoImpl implements LicenseDao {

    private final Map<String, Application> applicationStore;
    private final AtomicInteger llSequence = new AtomicInteger(1000);
    private final AtomicInteger dlSequence = new AtomicInteger(2000);

    public LicenseDaoImpl() {
        this.applicationStore = new HashMap<>();
    }

    // allows the store to be shared with RTOOfficerDaoImpl so both
    // layers operate on the same in-memory application records
    public LicenseDaoImpl(Map<String, Application> applicationStore) {
        this.applicationStore = applicationStore;
    }

    public Map<String, Application> getApplicationStore() {
        return applicationStore;
    }

    // US-003 (Mansidak)
    @Override
    public String createLLRequest(Application application) {
        if (application == null || application.getApplicant() == null) {
            return "Invalid application details";
        }
        String applicationNumber = "LL-" + llSequence.incrementAndGet();
        application.setApplicationNumber(applicationNumber);
        application.setStatus(ApplicationStatus.PENDING);
        applicationStore.put(applicationNumber, application);
        return "Application saved successfully";
    }

    // US-004 (Mansidak)
    @Override
    public Application getApplicationById(String applicationNumber) {
        return applicationStore.get(applicationNumber);
    }

    // US-007 (Himanshu)
    @Override
    public String createDLRequest(Application application) {
        if (application == null || application.getApplicant() == null) {
            return "Invalid application details";
        }
        String applicationNumber = "DL-" + dlSequence.incrementAndGet();
        application.setApplicationNumber(applicationNumber);
        application.setType(ApplicationType.DL);
        application.setStatus(ApplicationStatus.PENDING);
        applicationStore.put(applicationNumber, application);
        return "Application submitted successfully";
    }

    // US-008 (Himanshu)
    @Override
    public String scheduleTest(String applicationNumber, Date testDate) {
        Application application = applicationStore.get(applicationNumber);
        if (application == null) {
            return "Application not found";
        }
        if (application.getType() != ApplicationType.DL) {
            return "Test scheduling is only for Driving License applications";
        }
        application.setTestDate(testDate);
        applicationStore.put(applicationNumber, application);
        return "Test slot booked successfully";
    }

    // Returns the approved LL application for the given email, or any LL if none approved
    @Override
    public Application getLLApplicationByEmail(String email) {
        if (email == null) return null;
        String lowerEmail = email.trim().toLowerCase();
        Optional<Application> approved = applicationStore.values().stream()
                .filter(a -> a.getType() == ApplicationType.LL
                        && a.getApplicant() != null
                        && lowerEmail.equals(a.getApplicant().getEmail() != null
                                ? a.getApplicant().getEmail().toLowerCase() : "")
                        && a.getStatus() == ApplicationStatus.APPROVED)
                .findFirst();
        if (approved.isPresent()) return approved.get();
        return applicationStore.values().stream()
                .filter(a -> a.getType() == ApplicationType.LL
                        && a.getApplicant() != null
                        && lowerEmail.equals(a.getApplicant().getEmail() != null
                                ? a.getApplicant().getEmail().toLowerCase() : ""))
                .findFirst()
                .orElse(null);
    }
}
