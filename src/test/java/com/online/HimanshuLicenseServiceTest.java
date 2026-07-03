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

import static org.junit.jupiter.api.Assertions.*;

class HimanshuLicenseServiceTest {

    private LicenseService licenseService;
    private RTOOfficerService rtoOfficerService;
    private Application dlApplication;

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

        dlApplication = new Application(null, new Date(), "Online", 500.0,
                "PAID", null, null, null, applicant);
        licenseService.applyForDL(dlApplication);
    }

    // US-007: Applicant applies for driving license -> Application submitted successfully
    @Test
    void testApplyForDL_Success() {
        Applicant applicant = new Applicant(null, "Test User", "test@example.com",
                "9999999999", "Mumbai", "987654321012",
                LocalDate.of(1997, 1, 1), null, null);
        Application app = new Application(null, new Date(), "Online", 500.0,
                "PAID", null, null, null, applicant);

        String result = licenseService.applyForDL(app);

        assertEquals("Application submitted successfully", result);
        assertNotNull(app.getApplicationNumber());
        assertTrue(app.getApplicationNumber().startsWith("DL-"));
        assertEquals(ApplicationStatus.PENDING, app.getStatus());
    }

    @Test
    void testApplyForDL_InvalidApplication() {
        String result = licenseService.applyForDL(null);
        assertEquals("Invalid application details", result);
    }

    // US-008: Applicant schedules driving test -> Test slot booked successfully
    @Test
    void testScheduleDrivingTest_Success() {
        String result = licenseService.scheduleDrivingTest(dlApplication.getApplicationNumber(), new Date());
        assertEquals("Test slot booked successfully", result);
    }

    @Test
    void testScheduleDrivingTest_ApplicationNotFound() {
        String result = licenseService.scheduleDrivingTest("DL-UNKNOWN", new Date());
        assertEquals("Application not found", result);
    }

    // US-009: Applicant views DL application status -> Current status displayed
    @Test
    void testViewDLStatus_CurrentStatusDisplayed() {
        ApplicationStatus status = licenseService.viewDLStatus(dlApplication.getApplicationNumber());
        assertEquals(ApplicationStatus.PENDING, status);
    }

    @Test
    void testViewDLStatus_ApplicationNotFound() {
        ApplicationStatus status = licenseService.viewDLStatus("DL-UNKNOWN");
        assertNull(status);
    }

    // US-010: RTO officer approves driving license -> Application approved, license generation pending
    @Test
    void testApproveDrivingLicense_LicenseApprovedSuccessfully() {
        // Must pass the driving test before DL can be approved
        rtoOfficerService.passTest(dlApplication.getApplicationNumber());

        String result = rtoOfficerService.approveDrivingLicense(dlApplication.getApplicationNumber());
        assertEquals("DL application approved. License number generation is pending", result);
        assertNull(dlApplication.getApplicant().getDrivingLicenseNumber());

        ApplicationStatus status = licenseService.viewDLStatus(dlApplication.getApplicationNumber());
        assertEquals(ApplicationStatus.APPROVED, status);
    }

    @Test
    void testApproveDrivingLicense_ApplicationNotFound() {
        String result = rtoOfficerService.approveDrivingLicense("DL-UNKNOWN");
        assertEquals("Application not found", result);
    }

    // US-011: RTO officer rejects driving license -> License rejection recorded
    @Test
    void testRejectDrivingLicense_RejectionRecorded() {
        String result = rtoOfficerService.rejectDrivingLicense(dlApplication.getApplicationNumber());
        assertEquals("License rejection recorded", result);

        ApplicationStatus status = licenseService.viewDLStatus(dlApplication.getApplicationNumber());
        assertEquals(ApplicationStatus.REJECTED, status);
    }

    @Test
    void testRejectDrivingLicense_ApplicationNotFound() {
        String result = rtoOfficerService.rejectDrivingLicense("DL-UNKNOWN");
        assertEquals("Application not found", result);
    }
}
