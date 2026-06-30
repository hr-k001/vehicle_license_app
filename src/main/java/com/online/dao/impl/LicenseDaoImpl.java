package com.capgemini.dao.impl;

import com.capgemini.dao.LicenseDao;
import com.capgemini.model.Application;
import com.capgemini.model.ApplicationStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LicenseDaoImpl implements LicenseDao {

    // in-memory store: applicationNumber -> Application
    private final Map<String, Application> applicationStore;
    private final AtomicInteger sequence = new AtomicInteger(1000);

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

    @Override
    public String createLLRequest(Application application) {
        if (application == null || application.getApplicant() == null) {
            return "Invalid application details";
        }
        String applicationNumber = "LL-" + sequence.incrementAndGet();
        application.setApplicationNumber(applicationNumber);
        application.setStatus(ApplicationStatus.PENDING);
        applicationStore.put(applicationNumber, application);
        return "Application saved successfully";
    }

    @Override
    public Application getApplicationById(String applicationNumber) {
        return applicationStore.get(applicationNumber);
    }
}
