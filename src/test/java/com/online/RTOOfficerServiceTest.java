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

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RTOOfficerServiceTest {

    private LicenseService licenseService;
    private RTOOfficerService rtoOfficerService;
    private RTOOfficerDao rtoOfficerDao;
    private Application application;

    @BeforeEach
    void setUp() {
        LicenseDaoImpl licenseDao = new LicenseDaoImpl();
        rtoOfficerDao = new RTOOfficerDaoImpl(licenseDao.getApplicationStore());

        licenseService = new LicenseServiceImpl(licenseDao);
        rtoOfficerService = new RTOOfficerServiceImpl(rtoOfficerDao);

        Applicant applicant = new Applicant(
                null, "Himanshu Kumar", "himanshu@example.com",
                "9123456780", "Ludhiana", "123456789012",
                LocalDate.of(1998, 3, 10), null, null);

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

    @Test
    void testUpdateApplicationDetails_UpdatesEditableFields() {
        Application updated = new Application();
        updated.setStatus(ApplicationStatus.REJECTED);
        updated.setRemarks("Officer corrected the application details.");
        updated.setAmountPaid(350.0);
        updated.setPaymentStatus("PAID");
        updated.setTestResult("PASS");

        Applicant applicant = new Applicant();
        applicant.setFullName("Updated Name");
        applicant.setEmail("updated@example.com");
        applicant.setPhone("9988776655");
        updated.setApplicant(applicant);

        String result = rtoOfficerService.updateApplicationDetails(application.getApplicationNumber(), updated);

        assertEquals("Application updated successfully", result);

        Application stored = rtoOfficerDao.getApplicationById(application.getApplicationNumber());
        assertEquals(ApplicationStatus.REJECTED, stored.getStatus());
        assertEquals("Officer corrected the application details.", stored.getRemarks());
        assertEquals(350.0, stored.getAmountPaid());
        assertEquals("PAID", stored.getPaymentStatus());
        assertEquals("PASS", stored.getTestResult());
        assertEquals("Updated Name", stored.getApplicant().getFullName());
        assertEquals("updated@example.com", stored.getApplicant().getEmail());
        assertEquals("9988776655", stored.getApplicant().getPhone());
    }
}
