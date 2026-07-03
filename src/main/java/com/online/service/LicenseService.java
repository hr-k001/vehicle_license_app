package com.online.service;

import com.online.dto.LicenseDetailDTO;
import com.online.model.Application;
import com.online.model.ApplicationStatus;

import java.util.Date;

public interface LicenseService {

    // US-003
    String applyForLL(Application application);

    // US-004
    ApplicationStatus viewLLStatus(String applicationNumber);

    // US-007
    String applyForDL(Application application);

    // US-008
    String scheduleDrivingTest(String applicationNumber, Date testDate);

    // US-009
    ApplicationStatus viewDLStatus(String applicationNumber);

    String getDrivingLicenseNumberForApplication(String applicationNumber);

    // Gate check before DL submission
    Application getLLApplicationByEmail(String email);

    // US-015
    String generateLicenseNumber(String applicationId);

    // US-016
    LicenseDetailDTO viewLicenseDetails(String licenseNumber);
}
