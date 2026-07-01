package com.online.dao.impl;

import com.online.dao.RTOOfficerDao;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.DrivingLicense;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RTOOfficerDaoImpl implements RTOOfficerDao {

    private final Map<String, Application> applicationStore;
    private final AtomicInteger licenseSequence = new AtomicInteger(5000);

    public RTOOfficerDaoImpl(Map<String, Application> applicationStore) {
        this.applicationStore = applicationStore;
    }

    // US-005 (Mansidak)
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
        Date validTill = new Date(issueDate.getTime() + (180L * 24 * 60 * 60 * 1000));
        return new DrivingLicense(licenseNumber, issueDate, validTill);
    }

    // US-012 (Himanshu)
    @Override
    public List<Application> getAllApplications() {
        return new ArrayList<>(applicationStore.values());
    }

    @Override
    public String updateTestResult(String applicationNumber, String result) {
        Application application = applicationStore.get(applicationNumber);
        if (application == null) return "Application not found";
        application.setTestResult(result);
        applicationStore.put(applicationNumber, application);
        return "Test result updated to " + result;
    }

    // US-013 (Himanshu) — searches by application number, applicant email, or applicant name
    @Override
    public List<Application> searchApplications(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllApplications();
        }
        String lower = query.trim().toLowerCase();
        List<Application> results = new ArrayList<>();
        for (Application app : applicationStore.values()) {
            if (app.getApplicationNumber().toLowerCase().contains(lower)) {
                results.add(app);
                continue;
            }
            if (app.getApplicant() != null) {
                String email = app.getApplicant().getEmail();
                String name = app.getApplicant().getFirstName() + " " + app.getApplicant().getLastName();
                if ((email != null && email.toLowerCase().contains(lower))
                        || name.toLowerCase().contains(lower)) {
                    results.add(app);
                }
            }
        }
        return results;
    }
}
