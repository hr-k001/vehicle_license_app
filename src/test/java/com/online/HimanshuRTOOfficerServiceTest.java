package com.online;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HimanshuRTOOfficerServiceTest {

    private LicenseService licenseService;
    private RTOOfficerService rtoOfficerService;
    private Application llApplication;

    @BeforeEach
    void setUp() {
        LicenseDaoImpl licenseDao = new LicenseDaoImpl();
        RTOOfficerDao rtoOfficerDao = new RTOOfficerDaoImpl(licenseDao.getApplicationStore());

        licenseService = new LicenseServiceImpl(licenseDao);
        rtoOfficerService = new RTOOfficerServiceImpl(rtoOfficerDao);

        Applicant applicant = new Applicant(
                null, "Himanshu Kumar", "himanshu@example.com",
                "9123456780", "Delhi", "123456789012",
                LocalDate.of(1998, 3, 10), null, null);

        llApplication = new Application(null, new Date(), "Online", 200.0,
                "PAID", null, null, null, applicant);
        licenseService.applyForLL(llApplication);
    }

    // US-006: RTO officer rejects learner license -> Status updated to Rejected
    @Test
    void testRejectLearnerLicense_StatusUpdatedToRejected() {
        String result = rtoOfficerService.rejectLearnerLicense(llApplication.getApplicationNumber());

        assertEquals("Status updated to Rejected", result);

        ApplicationStatus status = licenseService.viewLLStatus(llApplication.getApplicationNumber());
        assertEquals(ApplicationStatus.REJECTED, status);
    }

    @Test
    void testRejectLearnerLicense_ApplicationNotFound() {
        String result = rtoOfficerService.rejectLearnerLicense("LL-UNKNOWN");
        assertEquals("Application not found", result);
    }

    // US-012: Officer views all applications -> Applications displayed correctly
    @Test
    void testGetAllApplications_DisplayedCorrectly() {
        List<Application> apps = rtoOfficerService.getAllApplications();
        assertFalse(apps.isEmpty());
        assertEquals(1, apps.size());
        assertEquals(llApplication.getApplicationNumber(), apps.get(0).getApplicationNumber());
    }

    // US-013: Officer searches applications -> Search results returned correctly
    @Test
    void testSearchApplications_ByEmail() {
        List<Application> results = rtoOfficerService.searchApplications("himanshu@example.com");
        assertFalse(results.isEmpty());
        assertEquals("himanshu@example.com", results.get(0).getApplicant().getEmail());
    }

    @Test
    void testSearchApplications_ByApplicationNumber() {
        List<Application> results = rtoOfficerService.searchApplications(llApplication.getApplicationNumber());
        assertEquals(1, results.size());
    }

    @Test
    void testSearchApplications_NoMatch() {
        List<Application> results = rtoOfficerService.searchApplications("nonexistent@xyz.com");
        assertTrue(results.isEmpty());
    }
}
