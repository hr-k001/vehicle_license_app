package com.online;

import com.online.dao.LicenseDao;
import com.online.dao.impl.LicenseDaoImpl;
import com.online.model.Applicant;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.ApplicationType;
import com.online.repository.ApplicantRepository;
import com.online.repository.LicenseRepository;
import com.online.service.LicenseService;
import com.online.service.impl.LicenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.Optional;

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
                null, "Mansidak Singh", "applicant@example.com",
                "9876543210", "Ludhiana", "123456789012",
                LocalDate.of(1995, 6, 15), null, null);

        return new Application(null, new java.util.Date(), "Online", 200.0,
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

    @Test
    void testGenerateLicenseNumber_UsesApplicantRepositorySafely() {
        LicenseDao licenseDao = new LicenseDaoImpl();
        Application application = buildSampleApplication();
        licenseDao.createDLRequest(application);
        application.setType(ApplicationType.DL);
        application.setStatus(ApplicationStatus.APPROVED);

        ApplicantRepository applicantRepository = (ApplicantRepository) Proxy.newProxyInstance(
                ApplicantRepository.class.getClassLoader(),
                new Class[]{ApplicantRepository.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "findByAadhaarNumber":
                        case "findByEmail":
                        case "findByLearnerLicenseNumber":
                        case "findByDrivingLicenseNumber":
                            return Optional.empty();
                        case "save":
                            return args[0];
                        default:
                            return defaultValue(method.getReturnType());
                    }
                }
        );

        LicenseRepository licenseRepository = (LicenseRepository) Proxy.newProxyInstance(
                LicenseRepository.class.getClassLoader(),
                new Class[]{LicenseRepository.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "findByDrivingLicenseNumber":
                        case "findByApplicant_ApplicantId":
                            return Optional.empty();
                        case "save":
                            return args[0];
                        default:
                            return defaultValue(method.getReturnType());
                    }
                }
        );

        LicenseService service = new LicenseServiceImpl(licenseDao, licenseRepository, applicantRepository);
        String result = service.generateLicenseNumber(application.getApplicationNumber());

        assertTrue(result.startsWith("License generated successfully:"));
        assertNotNull(application.getApplicant().getDrivingLicenseNumber());
    }

    private Object defaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) return false;
            if (type == char.class) return '\0';
            if (type == byte.class) return (byte) 0;
            if (type == short.class) return (short) 0;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == float.class) return 0f;
            if (type == double.class) return 0d;
        }
        return null;
    }
}
