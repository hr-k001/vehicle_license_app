package com.online.service;

import com.online.model.Application;
import com.online.model.ApplicationStatus;

import java.util.Date;

public interface LicenseService {

    // Mansidak — US-003
    String applyForLL(Application application);

    // Mansidak — US-004
    ApplicationStatus viewLLStatus(String applicationNumber);

    // Himanshu — US-007
    String applyForDL(Application application);

    // Himanshu — US-008
    String scheduleDrivingTest(String applicationNumber, Date testDate);

    // Himanshu — US-009
    ApplicationStatus viewDLStatus(String applicationNumber);

    // Returns the LL application for the given email (to gate DL submission)
    Application getLLApplicationByEmail(String email);
}
