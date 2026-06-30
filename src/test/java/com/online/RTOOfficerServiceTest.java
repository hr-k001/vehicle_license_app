package com.capgemini;

import com.capgemini.dao.LicenseDao;
import com.capgemini.dao.RTOOfficerDao;
import com.capgemini.dao.impl.LicenseDaoImpl;
import com.capgemini.dao.impl.RTOOfficerDaoImpl;
import com.capgemini.model.Applicant;
import com.capgemini.model.Application;
import com.capgemini.model.ApplicationStatus;
import com.capgemini.service.LicenseService;
import com.capgemini.service.RTOOfficerService;
import com.capgemini.service.impl.LicenseServiceImpl;
import com.capgemini.service.impl.RTOOfficerServiceImpl;
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
