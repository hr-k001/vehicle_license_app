package com.online.service.impl;

import com.online.dao.LicenseDao;
import com.online.dto.LicenseDetailDTO;
import com.online.model.*;
import com.online.repository.DrivingLicenseRepository;
import com.online.service.LicenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("License Service Tests")
class LicenseServiceTest {

    @Mock private LicenseDao licenseDao;
    @Mock private DrivingLicenseRepository drivingLicenseRepository;

    private LicenseService licenseService;
    private Applicant testApplicant;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        licenseService = new LicenseServiceImpl(licenseDao);
        testApplicant = Applicant.builder().applicantId(1L).fullName("Test Applicant")
                .email("test@example.com").phone("9876543210").address("123 Test Street")
                .aadhaarNumber("123456789012").dateOfBirth(LocalDate.of(2000, 1, 15)).build();
        testApplication = new Application();
        testApplication.setApplicationNumber("LL-1001");
        testApplication.setApplicant(testApplicant);
        testApplication.setType(ApplicationType.LL);
        testApplication.setStatus(ApplicationStatus.PENDING);
        testApplication.setApplicationDate(new Date());
        testApplication.setAmountPaid(200.0);
        testApplication.setPaymentStatus("PAID");
    }

    @Test @DisplayName("Should apply for LL successfully")
    void testApplyForLL_Success() {
        when(licenseDao.createLLRequest(any())).thenReturn("Application saved successfully");
        String result = licenseService.applyForLL(testApplication);
        assertEquals("Application saved successfully", result);
        assertEquals(ApplicationType.LL, testApplication.getType());
        verify(licenseDao).createLLRequest(any());
    }

    @Test @DisplayName("Should return error for null LL application")
    void testApplyForLL_Null() {
        assertEquals("Invalid application details", licenseService.applyForLL(null));
    }

    @Test @DisplayName("Should view LL status")
    void testViewLLStatus_Success() {
        when(licenseDao.getApplicationById("LL-1001")).thenReturn(testApplication);
        assertEquals(ApplicationStatus.PENDING, licenseService.viewLLStatus("LL-1001"));
    }

    @Test @DisplayName("Should return null for unknown LL")
    void testViewLLStatus_NotFound() {
        when(licenseDao.getApplicationById("LL-9999")).thenReturn(null);
        assertNull(licenseService.viewLLStatus("LL-9999"));
    }

    @Test @DisplayName("Should apply for DL successfully")
    void testApplyForDL_Success() {
        Application dlApp = new Application();
        dlApp.setApplicant(testApplicant);
        when(licenseDao.createDLRequest(any())).thenReturn("Application submitted successfully");
        String result = licenseService.applyForDL(dlApp);
        assertEquals("Application submitted successfully", result);
        assertEquals(ApplicationType.DL, dlApp.getType());
    }

    @Test @DisplayName("Should return error for null DL application")
    void testApplyForDL_Null() {
        assertEquals("Invalid application details", licenseService.applyForDL(null));
    }

    @Test @DisplayName("Should view DL status")
    void testViewDLStatus_Success() {
        Application dlApp = new Application();
        dlApp.setType(ApplicationType.DL);
        dlApp.setStatus(ApplicationStatus.PENDING);
        when(licenseDao.getApplicationById("DL-2001")).thenReturn(dlApp);
        assertEquals(ApplicationStatus.PENDING, licenseService.viewDLStatus("DL-2001"));
    }

    @Test @DisplayName("Should return null for LL when checking DL status")
    void testViewDLStatus_WrongType() {
        when(licenseDao.getApplicationById("LL-1001")).thenReturn(testApplication);
        assertNull(licenseService.viewDLStatus("LL-1001"));
    }

    @Test @DisplayName("Should schedule driving test")
    void testScheduleDrivingTest_Success() {
        Date d = new Date();
        when(licenseDao.scheduleTest("DL-2001", d)).thenReturn("Test slot booked successfully");
        assertEquals("Test slot booked successfully", licenseService.scheduleDrivingTest("DL-2001", d));
    }

    @Test @DisplayName("Should return error for null scheduling details")
    void testScheduleDrivingTest_Null() {
        assertEquals("Invalid scheduling details", licenseService.scheduleDrivingTest(null, null));
    }

    @Test @DisplayName("Should generate license number for approved DL")
    void testGenerateLicenseNumber_Success() {
        Application approvedDL = new Application();
        approvedDL.setApplicationNumber("DL-2001");
        approvedDL.setType(ApplicationType.DL);
        approvedDL.setStatus(ApplicationStatus.APPROVED);
        approvedDL.setApplicant(testApplicant);
        when(licenseDao.getApplicationById("DL-2001")).thenReturn(approvedDL);
        String result = licenseService.generateLicenseNumber("DL-2001");
        assertTrue(result.contains("successfully"));
    }

    @Test
    @DisplayName("Should persist generated license number in DrivingLicense entity")
    void testGenerateLicenseNumber_PersistsDrivingLicenseEntity() {
        Application approvedDL = new Application();
        approvedDL.setApplicationNumber("DL-2001");
        approvedDL.setType(ApplicationType.DL);
        approvedDL.setStatus(ApplicationStatus.APPROVED);
        approvedDL.setApplicant(testApplicant);
        when(licenseDao.getApplicationById("DL-2001")).thenReturn(approvedDL);

        licenseService = new LicenseServiceImpl(licenseDao, drivingLicenseRepository);

        String result = licenseService.generateLicenseNumber("DL-2001");

        assertTrue(result.contains("successfully"));
        verify(drivingLicenseRepository).save(any(DrivingLicense.class));
    }

    @Test @DisplayName("Should fail generate for unknown application")
    void testGenerateLicenseNumber_NotFound() {
        when(licenseDao.getApplicationById("DL-9999")).thenReturn(null);
        assertTrue(licenseService.generateLicenseNumber("DL-9999").contains("not found"));
    }

    @Test @DisplayName("Should fail generate for non-DL type")
    void testGenerateLicenseNumber_WrongType() {
        when(licenseDao.getApplicationById("LL-1001")).thenReturn(testApplication);
        assertTrue(licenseService.generateLicenseNumber("LL-1001").contains("Invalid application type"));
    }

    @Test @DisplayName("Should fail generate for non-approved application")
    void testGenerateLicenseNumber_NotApproved() {
        Application pending = new Application();
        pending.setType(ApplicationType.DL);
        pending.setStatus(ApplicationStatus.PENDING);
        pending.setApplicant(testApplicant);
        when(licenseDao.getApplicationById("DL-2001")).thenReturn(pending);
        assertTrue(licenseService.generateLicenseNumber("DL-2001").contains("must be approved"));
    }

    @Test @DisplayName("Should return null when repository is unavailable")
    void testViewLicenseDetails_RepositoryUnavailable() {
        LicenseDetailDTO result = licenseService.viewLicenseDetails("DL-2001");
        assertNull(result);
    }

    @Test
    @DisplayName("Should return license details from repository by license number")
    void testViewLicenseDetails_ByLicenseNumber() {
        DrivingLicense license = DrivingLicense.builder()
                .drivingLicenseNumber("DL-2026-000001")
                .dateOfIssue(LocalDate.of(2026, 1, 1))
                .validTill(LocalDate.of(2036, 1, 1))
                .applicant(testApplicant)
                .issueAuthority("RTO")
                .remarks("Generated automatically")
                .build();

        when(drivingLicenseRepository.findByDrivingLicenseNumber("DL-2026-000001")).thenReturn(Optional.of(license));
        licenseService = new LicenseServiceImpl(licenseDao, drivingLicenseRepository);

        LicenseDetailDTO result = licenseService.viewLicenseDetails("DL-2026-000001");

        assertNotNull(result);
        assertEquals("Test Applicant", result.getApplicantName());
        assertEquals("DL-2026-000001", result.getLicenseNumber());
        assertEquals("LMV", result.getVehicleCategory());
        assertEquals("ISSUED", result.getStatus());
    }

    @Test @DisplayName("Should return null for unknown license number")
    void testViewLicenseDetails_NotFound() {
        when(drivingLicenseRepository.findByDrivingLicenseNumber("DL-9999")).thenReturn(Optional.empty());
        licenseService = new LicenseServiceImpl(licenseDao, drivingLicenseRepository);

        assertNull(licenseService.viewLicenseDetails("DL-9999"));
    }
}
