package com.online.service.impl;

import com.online.dao.LicenseDao;
import com.online.dto.ApplicationReportDTO;
import com.online.dto.CountReportDTO;
import com.online.model.*;
import com.online.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReportServiceTest - Unit tests for ReportServiceImpl
 * Tests application reporting and statistics functionality
 * Uses JUnit 5 and Mockito for testing
 * Target: 90%+ code coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Report Service Tests")
class ReportServiceTest {

    @Mock
    private LicenseDao licenseDao;

    private ReportService reportService;
    private Map<String, Application> mockApplicationStore;
    private Applicant testApplicant;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(licenseDao);
        mockApplicationStore = new HashMap<>();
        testApplicant = Applicant.builder()
                .applicantId(1L)
                .fullName("Test Applicant")
                .email("test@example.com")
                .phone("9876543210")
                .address("123 Test Street")
                .aadhaarNumber("123456789012")
                .build();
    }

    // ============ GET ALL APPLICATIONS TESTS ============

    @Test
    @DisplayName("Should retrieve all applications successfully")
    void testGetAllApplications_Success() {
        // Arrange
        Application app1 = createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.PENDING);
        Application app2 = createApplication("LL-1002", ApplicationType.LL, ApplicationStatus.APPROVED);
        Application app3 = createApplication("DL-2001", ApplicationType.DL, ApplicationStatus.REJECTED);

        mockApplicationStore.put("LL-1001", app1);
        mockApplicationStore.put("LL-1002", app2);
        mockApplicationStore.put("DL-2001", app3);

        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getAllApplications();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("LL", result.get(0).getType());
        verify(licenseDao, times(1)).getApplicationStore();
    }

    @Test
    @DisplayName("Should return empty list when no applications exist")
    void testGetAllApplications_Empty() {
        // Arrange
        when(licenseDao.getApplicationStore()).thenReturn(new HashMap<>());

        // Act
        List<ApplicationReportDTO> result = reportService.getAllApplications();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(licenseDao, times(1)).getApplicationStore();
    }

    @Test
    @DisplayName("Should handle null applicant in application report")
    void testGetAllApplications_NullApplicant() {
        // Arrange
        Application appWithoutApplicant = new Application();
        appWithoutApplicant.setApplicationNumber("LL-1005");
        appWithoutApplicant.setType(ApplicationType.LL);
        appWithoutApplicant.setStatus(ApplicationStatus.PENDING);
        appWithoutApplicant.setApplicant(null);

        mockApplicationStore.put("LL-1005", appWithoutApplicant);
        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getAllApplications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("N/A", result.get(0).getApplicantName());
        assertEquals("N/A", result.get(0).getApplicantEmail());
    }

    // ============ GET APPROVED APPLICATIONS TESTS ============

    @Test
    @DisplayName("Should retrieve only approved applications")
    void testGetApprovedApplications_Success() {
        // Arrange
        Application approved1 = createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.APPROVED);
        Application approved2 = createApplication("DL-2001", ApplicationType.DL, ApplicationStatus.APPROVED);
        Application pending = createApplication("LL-1002", ApplicationType.LL, ApplicationStatus.PENDING);

        mockApplicationStore.put("LL-1001", approved1);
        mockApplicationStore.put("DL-2001", approved2);
        mockApplicationStore.put("LL-1002", pending);

        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getApprovedApplications();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(app -> "APPROVED".equals(app.getStatus())));
        verify(licenseDao, times(1)).getApplicationStore();
    }

    @Test
    @DisplayName("Should return empty list when no approved applications exist")
    void testGetApprovedApplications_Empty() {
        // Arrange
        mockApplicationStore.put("LL-1001", createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.PENDING));
        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getApprovedApplications();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ============ GET REJECTED APPLICATIONS TESTS ============

    @Test
    @DisplayName("Should retrieve only rejected applications")
    void testGetRejectedApplications_Success() {
        // Arrange
        Application rejected1 = createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.REJECTED);
        Application rejected2 = createApplication("DL-2001", ApplicationType.DL, ApplicationStatus.REJECTED);
        Application pending = createApplication("LL-1002", ApplicationType.LL, ApplicationStatus.PENDING);

        mockApplicationStore.put("LL-1001", rejected1);
        mockApplicationStore.put("DL-2001", rejected2);
        mockApplicationStore.put("LL-1002", pending);

        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getRejectedApplications();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(app -> "REJECTED".equals(app.getStatus())));
        verify(licenseDao, times(1)).getApplicationStore();
    }

    @Test
    @DisplayName("Should return empty list when no rejected applications exist")
    void testGetRejectedApplications_Empty() {
        // Arrange
        mockApplicationStore.put("LL-1001", createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.APPROVED));
        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getRejectedApplications();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ============ GET PENDING APPLICATIONS TESTS ============

    @Test
    @DisplayName("Should retrieve only pending applications")
    void testGetPendingApplications_Success() {
        // Arrange
        Application pending1 = createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.PENDING);
        Application pending2 = createApplication("DL-2001", ApplicationType.DL, ApplicationStatus.PENDING);
        Application approved = createApplication("LL-1002", ApplicationType.LL, ApplicationStatus.APPROVED);

        mockApplicationStore.put("LL-1001", pending1);
        mockApplicationStore.put("DL-2001", pending2);
        mockApplicationStore.put("LL-1002", approved);

        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getPendingApplications();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(app -> "PENDING".equals(app.getStatus())));
        verify(licenseDao, times(1)).getApplicationStore();
    }

    @Test
    @DisplayName("Should return empty list when no pending applications exist")
    void testGetPendingApplications_Empty() {
        // Arrange
        mockApplicationStore.put("LL-1001", createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.APPROVED));
        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        List<ApplicationReportDTO> result = reportService.getPendingApplications();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ============ GET APPLICATION COUNTS TESTS ============

    @Test
    @DisplayName("Should calculate application count statistics successfully")
    void testGetApplicationCounts_Success() {
        // Arrange
        mockApplicationStore.put("LL-1001", createApplication("LL-1001", ApplicationType.LL, ApplicationStatus.PENDING));
        mockApplicationStore.put("LL-1002", createApplication("LL-1002", ApplicationType.LL, ApplicationStatus.APPROVED));
        mockApplicationStore.put("LL-1003", createApplication("LL-1003", ApplicationType.LL, ApplicationStatus.REJECTED));
        mockApplicationStore.put("DL-2001", createApplication("DL-2001", ApplicationType.DL, ApplicationStatus.APPROVED));
        mockApplicationStore.put("DL-2002", createApplication("DL-2002", ApplicationType.DL, ApplicationStatus.PENDING));

        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        CountReportDTO result = reportService.getApplicationCounts();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getTotalApplications());
        assertEquals(2, result.getApprovedCount());
        assertEquals(1, result.getRejectedCount());
        assertEquals(2, result.getPendingCount());
        assertEquals(3, result.getLearnerLicenseCount());
        assertEquals(2, result.getDrivingLicenseCount());
        verify(licenseDao, times(1)).getApplicationStore();
    }

    @Test
    @DisplayName("Should return zero counts for empty application store")
    void testGetApplicationCounts_Empty() {
        // Arrange
        when(licenseDao.getApplicationStore()).thenReturn(new HashMap<>());

        // Act
        CountReportDTO result = reportService.getApplicationCounts();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalApplications());
        assertEquals(0, result.getApprovedCount());
        assertEquals(0, result.getRejectedCount());
        assertEquals(0, result.getPendingCount());
        assertEquals(0, result.getLearnerLicenseCount());
        assertEquals(0, result.getDrivingLicenseCount());
    }

    @Test
    @DisplayName("Should handle mixed application types correctly in counts")
    void testGetApplicationCounts_MixedTypes() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            mockApplicationStore.put("LL-100" + i,
                    createApplication("LL-100" + i, ApplicationType.LL, ApplicationStatus.PENDING));
        }
        for (int i = 1; i <= 3; i++) {
            mockApplicationStore.put("DL-200" + i,
                    createApplication("DL-200" + i, ApplicationType.DL, ApplicationStatus.APPROVED));
        }

        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        CountReportDTO result = reportService.getApplicationCounts();

        // Assert
        assertEquals(8, result.getTotalApplications());
        assertEquals(3, result.getDrivingLicenseCount());
        assertEquals(5, result.getLearnerLicenseCount());
        assertEquals(0, result.getRejectedCount());
    }

    @Test
    @DisplayName("Should separate status counts accurately")
    void testGetApplicationCounts_AccurateStatuses() {
        // Arrange
        mockApplicationStore.put("APP-001", createApplication("APP-001", ApplicationType.LL, ApplicationStatus.PENDING));
        mockApplicationStore.put("APP-002", createApplication("APP-002", ApplicationType.LL, ApplicationStatus.PENDING));
        mockApplicationStore.put("APP-003", createApplication("APP-003", ApplicationType.LL, ApplicationStatus.PENDING));
        mockApplicationStore.put("APP-004", createApplication("APP-004", ApplicationType.LL, ApplicationStatus.APPROVED));
        mockApplicationStore.put("APP-005", createApplication("APP-005", ApplicationType.LL, ApplicationStatus.APPROVED));
        mockApplicationStore.put("APP-006", createApplication("APP-006", ApplicationType.LL, ApplicationStatus.REJECTED));

        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        CountReportDTO result = reportService.getApplicationCounts();

        // Assert
        assertEquals(6, result.getTotalApplications());
        assertEquals(3, result.getPendingCount());
        assertEquals(2, result.getApprovedCount());
        assertEquals(1, result.getRejectedCount());
    }

    @Test
    @DisplayName("Should handle null status in application")
    void testGetApplicationCounts_NullStatus() {
        // Arrange
        Application app = new Application();
        app.setApplicationNumber("APP-001");
        app.setType(ApplicationType.LL);
        app.setStatus(null);
        app.setApplicant(testApplicant);

        mockApplicationStore.put("APP-001", app);
        when(licenseDao.getApplicationStore()).thenReturn(mockApplicationStore);

        // Act
        CountReportDTO result = reportService.getApplicationCounts();

        // Assert
        assertEquals(1, result.getTotalApplications());
        verify(licenseDao, times(1)).getApplicationStore();
    }

    // ============ HELPER METHOD ============

    private Application createApplication(String appNumber, ApplicationType type, ApplicationStatus status) {
        Application app = new Application();
        app.setApplicationNumber(appNumber);
        app.setType(type);
        app.setStatus(status);
        app.setApplicant(testApplicant);
        app.setApplicationDate(new Date());
        app.setAmountPaid(type == ApplicationType.LL ? 200.0 : 500.0);
        app.setPaymentStatus("PAID");
        return app;
    }
}
