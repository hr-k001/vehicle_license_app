package com.online;

import com.online.dao.LicenseDao;
import com.online.dao.RTOOfficerDao;
import com.online.dao.impl.LicenseDaoImpl;
import com.online.dao.impl.RTOOfficerDaoImpl;
import com.online.model.Applicant;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.service.LicenseService;
import com.online.service.RTOOfficerService;
import com.online.service.impl.LicenseServiceImpl;
import com.online.service.impl.RTOOfficerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RTOOfficerServiceTest {

    private LicenseService licenseService;
    private RTOOfficerService rtoOfficerService;
    private Application application;

    @BeforeEach
    void setUp() {
        // LicenseDaoImpl and RTOOfficerDaoImpl share the same in-memory
        // application store so an officer can act on applicant-submitted applications
        LicenseDaoImpl licenseDao = new LicenseDaoImpl();
        RTOOfficerDao rtoOfficerDao = new RTOOfficerDaoImpl(licenseDao.getApplicationStore());

        licenseService = new LicenseServiceImpl(licenseDao);
        rtoOfficerService = new RTOOfficerServiceImpl(rtoOfficerDao);

        Applicant applicant = new Applicant(
                "Himanshu", null, "Kumar", new Date(),
                "Ludhiana", "Graduate", "9123456780",
                "himanshu@example.com", "Indian", "Two Wheeler", null);

        application = new Application(null, new Date(), "Online", 200.0,
                "PAID", null, null, null, applicant);

        licenseService.applyForLL(application);
    }

    // US-005: RTO officer approves learner license -> Status updated to Approved
    @Test
    void testApproveLearnerLicense_StatusUpdatedToApproved() {
        String result = rtoOfficerService.approveLearnerLicense(application.getApplicationNumber());

        assertEquals("Status updated to APPROVED", result);

        ApplicationStatus status = licenseService.viewLLStatus(application.getApplicationNumber());
        assertEquals(ApplicationStatus.APPROVED, status);
    }

    @Test
    void testApproveLearnerLicense_ApplicationNotFound() {
        String result = rtoOfficerService.approveLearnerLicense("LL-UNKNOWN");
        assertEquals("Application not found", result);
    }
}
