package com.online.dao;

import com.online.model.Application;
import com.online.model.DrivingLicense;

import java.util.List;

public interface RTOOfficerDao {

    // Mansidak — US-005
    Application getApplicationById(String applicationNumber);

    String updateApplicationById(String applicationNumber, Application application);

    DrivingLicense createLearnerLicense(String applicationNumber);

    // Himanshu — US-012
    List<Application> getAllApplications();

    // Himanshu — US-013
    List<Application> searchApplications(String query);

    // Mark driving test result for a DL application
    String updateTestResult(String applicationNumber, String result);
}
