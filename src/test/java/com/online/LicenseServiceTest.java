package com.capgemini;

import com.capgemini.dao.LicenseDao;
import com.capgemini.dao.impl.LicenseDaoImpl;
import com.capgemini.model.Applicant;
import com.capgemini.model.Application;
import com.capgemini.model.ApplicationStatus;
import com.capgemini.service.LicenseService;
import com.capgemini.service.impl.LicenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class LicenseServiceTest {

    private LicenseService licenseService;

    @BeforeEach
    void setUp() {
        LicenseDao licenseDao = new LicenseDaoImpl();
        licenseService = new LicenseServiceImpl(licenseDao);
    }

    private Application buildSampleApplication() {
        Applicant applicant = new Applicant(
                "Mansidak", null, "Singh", new Date(),
                "Ludhiana", "Graduate", "9876543210",
                "applicant@example.com", "Indian", "Two Wheeler", null);

        return new Application(null, new Date(), "Online", 200.0,
                "PAID", null, null, null, applicant);
    }

    // US-003: Applicant submits learner license application -> Application saved successfully
    @Test
    void testApplyForLL_Success() {
        Application application = buildSampleApplication();
        String result = licenseService.applyForLL(application);
        assertEquals("Application saved successfully", result);
        assertNotNull(application.getApplicationNumber());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
    }

    @Test
    void testApplyForLL_InvalidApplication() {
        String result = licenseService.applyForLL(null);
        assertEquals("Invalid application details", result);
    }

    // US-004: Applicant checks learner license status -> Correct status displayed
    @Test
    void testViewLLStatus_CorrectStatusDisplayed() {
        Application application = buildSampleApplication();
        licenseService.applyForLL(application);

        ApplicationStatus status = licenseService.viewLLStatus(application.getApplicationNumber());
        assertEquals(ApplicationStatus.PENDING, status);
    }

    @Test
    void testViewLLStatus_ApplicationNotFound() {
        ApplicationStatus status = licenseService.viewLLStatus("LL-UNKNOWN");
        assertNull(status);
    }
}
