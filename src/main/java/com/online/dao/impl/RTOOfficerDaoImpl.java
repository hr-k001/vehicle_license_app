package com.online.dao.impl;

import com.online.dao.RTOOfficerDao;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.DrivingLicense;
import com.online.repository.ApplicationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RTOOfficerDaoImpl implements RTOOfficerDao {

    private final Map<String, Application> applicationStore;
    private final ApplicationRepository applicationRepository;
    private final AtomicInteger licenseSequence = new AtomicInteger(5000);

    public RTOOfficerDaoImpl(Map<String, Application> applicationStore) {
        this(applicationStore, null);
    }

    public RTOOfficerDaoImpl(Map<String, Application> applicationStore, ApplicationRepository applicationRepository) {
        this.applicationStore = applicationStore;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Application getApplicationById(String applicationNumber) {
        if (applicationRepository != null) {
            applicationRepository.findById(applicationNumber).ifPresent(app -> applicationStore.put(applicationNumber, app));
        }
        return applicationStore.get(applicationNumber);
    }

    @Override
    public String updateApplicationById(String applicationNumber, Application application) {
        if (!applicationStore.containsKey(applicationNumber)) return "Application not found";
        applicationStore.put(applicationNumber, application);
        if (applicationRepository != null) {
            applicationRepository.save(application);
        }
        return "Status updated to " + application.getStatus();
    }

    @Override
    public DrivingLicense createLearnerLicense(String applicationNumber) {
        Application application = applicationStore.get(applicationNumber);
        if (application == null || application.getStatus() != ApplicationStatus.APPROVED) return null;
        String licenseNumber = "LL-LIC-" + licenseSequence.incrementAndGet();
        return DrivingLicense.builder()
                .drivingLicenseNumber(licenseNumber)
                .dateOfIssue(LocalDate.now())
                .validTill(LocalDate.now().plusMonths(6))
                .build();
    }

    @Override
    public List<Application> getAllApplications() {
        if (applicationRepository != null) {
            applicationStore.clear();
            applicationRepository.findAll().forEach(app -> applicationStore.put(app.getApplicationNumber(), app));
        }
        return new ArrayList<>(applicationStore.values());
    }

    @Override
    public String updateTestResult(String applicationNumber, String result) {
        Application application = applicationStore.get(applicationNumber);
        if (application == null) return "Application not found";
        application.setTestResult(result);
        applicationStore.put(applicationNumber, application);
        if (applicationRepository != null) {
            applicationRepository.save(application);
        }
        return "Test result updated to " + result;
    }

    // US-013: search by application number, email, or name
    @Override
    public List<Application> searchApplications(String query) {
        if (query == null || query.trim().isEmpty()) return getAllApplications();
        String lower = query.trim().toLowerCase();
        List<Application> results = new ArrayList<>();
        for (Application app : applicationStore.values()) {
            if (app.getApplicationNumber().toLowerCase().contains(lower)) {
                results.add(app); continue;
            }
            if (app.getApplicant() != null) {
                String email = app.getApplicant().getEmail();
                String name  = app.getApplicant().getFullName();
                if ((email != null && email.toLowerCase().contains(lower))
                        || (name != null && name.toLowerCase().contains(lower))) {
                    results.add(app);
                }
            }
        }
        return results;
    }
}
