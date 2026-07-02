package com.online.service;

import com.online.model.Application;

import java.util.List;

public interface RTOOfficerService {

    // Mansidak — US-005
    String approveLearnerLicense(String applicationNumber);

    // Himanshu — US-006
    String rejectLearnerLicense(String applicationNumber);

    // Himanshu — US-010
    String approveDrivingLicense(String applicationNumber);

    // Himanshu — US-011
    String rejectDrivingLicense(String applicationNumber);

    // Himanshu — US-012
    List<Application> getAllApplications();

    // Himanshu — US-013
    List<Application> searchApplications(String query);

    String updateApplicationDetails(String applicationNumber, Application updatedApplication);

    // RTO marks driving test as passed or failed
    String passTest(String applicationNumber);
    String failTest(String applicationNumber);
}
