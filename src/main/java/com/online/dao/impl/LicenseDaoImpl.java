package com.online.dao.impl;

import com.online.dao.LicenseDao;
import com.online.model.Applicant;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.ApplicationType;
import com.online.repository.ApplicantRepository;
import com.online.repository.ApplicationRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LicenseDaoImpl implements LicenseDao {

    private final Map<String, Application> applicationStore;
    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final AtomicInteger llSequence = new AtomicInteger(1000);
    private final AtomicInteger dlSequence = new AtomicInteger(2000);

    public LicenseDaoImpl() {
        this(null, null);
    }

    public LicenseDaoImpl(Map<String, Application> applicationStore) {
        this.applicationStore = applicationStore;
        this.applicationRepository = null;
        this.applicantRepository = null;
    }

    public LicenseDaoImpl(ApplicationRepository applicationRepository, ApplicantRepository applicantRepository) {
        this.applicationStore = new HashMap<>();
        this.applicationRepository = applicationRepository;
        this.applicantRepository = applicantRepository;
        if (applicationRepository != null) {
            applicationRepository.findAll().forEach(app -> applicationStore.put(app.getApplicationNumber(), app));
            syncSequences();
        }
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
        saveApplication(appNo, application);
        return "Application saved successfully";
    }

    // US-004
    @Override
    public Application getApplicationById(String applicationNumber) {
        if (applicationRepository != null) {
            applicationRepository.findById(applicationNumber).ifPresent(app -> applicationStore.put(applicationNumber, app));
        }
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
        saveApplication(appNo, application);
        return "Application submitted successfully";
    }

    // US-008
    @Override
    public String scheduleTest(String applicationNumber, Date testDate) {
        Application application = applicationStore.get(applicationNumber);
        if (application == null) return "Application not found";
        if (application.getType() != ApplicationType.DL) return "Test scheduling is only for Driving License applications";
        application.setTestDate(testDate);
        saveApplication(applicationNumber, application);
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

    private void saveApplication(String applicationNumber, Application application) {
        attachExistingApplicant(application);
        applicationStore.put(applicationNumber, application);
        if (applicationRepository != null) {
            applicationRepository.save(application);
        }
    }

    private void attachExistingApplicant(Application application) {
        if (applicantRepository == null || application.getApplicant() == null) return;
        Applicant incoming = application.getApplicant();
        Optional<Applicant> existing = Optional.empty();
        if (incoming.getAadhaarNumber() != null) {
            existing = applicantRepository.findByAadhaarNumber(incoming.getAadhaarNumber());
        }
        if (existing.isEmpty() && incoming.getEmail() != null) {
            existing = applicantRepository.findByEmail(incoming.getEmail());
        }
        existing.ifPresentOrElse(stored -> {
            stored.setFullName(incoming.getFullName());
            stored.setEmail(incoming.getEmail());
            stored.setPhone(incoming.getPhone());
            stored.setAddress(incoming.getAddress());
            stored.setAadhaarNumber(incoming.getAadhaarNumber());
            stored.setDateOfBirth(incoming.getDateOfBirth());
            stored.setVehicleType(incoming.getVehicleType());
            application.setApplicant(stored);
        }, () -> application.setApplicant(applicantRepository.save(incoming)));
    }

    private void syncSequences() {
        applicationStore.keySet().forEach(appNo -> {
            if (appNo != null && appNo.startsWith("LL-")) {
                parseNumber(appNo).ifPresent(n -> llSequence.set(Math.max(llSequence.get(), n)));
            }
            if (appNo != null && appNo.startsWith("DL-")) {
                parseNumber(appNo).ifPresent(n -> dlSequence.set(Math.max(dlSequence.get(), n)));
            }
        });
    }

    private Optional<Integer> parseNumber(String appNo) {
        try {
            return Optional.of(Integer.parseInt(appNo.substring(3)));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
