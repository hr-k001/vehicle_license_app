package com.online.dao;

import com.online.model.Application;

import java.util.Date;

public interface LicenseDao {

    // Mansidak — US-003
    String createLLRequest(Application application);

    // Mansidak — US-004
    Application getApplicationById(String applicationNumber);

    // Himanshu — US-007
    String createDLRequest(Application application);

    // Himanshu — US-008
    String scheduleTest(String applicationNumber, Date testDate);

    // Check if an applicant has an approved LL (needed before DL application)
    Application getLLApplicationByEmail(String email);

    // Expose the in-memory store for reporting (US-018)
    java.util.Map<String, Application> getApplicationStore();
}
