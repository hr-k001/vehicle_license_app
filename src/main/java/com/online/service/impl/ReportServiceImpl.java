package com.online.service.impl;

import com.online.dao.LicenseDao;
import com.online.dto.ApplicationReportDTO;
import com.online.dto.CountReportDTO;
import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.model.ApplicationType;
import com.online.service.ReportService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// US-018: Application Reports
public class ReportServiceImpl implements ReportService {

    private final LicenseDao licenseDao;

    public ReportServiceImpl(LicenseDao licenseDao) {
        this.licenseDao = licenseDao;
    }

    @Override
    public List<ApplicationReportDTO> getAllApplications() {
        return licenseDao.getApplicationStore().values().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationReportDTO> getApprovedApplications() {
        return licenseDao.getApplicationStore().values().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationReportDTO> getRejectedApplications() {
        return licenseDao.getApplicationStore().values().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED)
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationReportDTO> getPendingApplications() {
        return licenseDao.getApplicationStore().values().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public CountReportDTO getApplicationCounts() {
        Map<String, Application> store = licenseDao.getApplicationStore();
        return CountReportDTO.builder()
                .totalApplications(store.size())
                .approvedCount(store.values().stream().filter(a -> a.getStatus() == ApplicationStatus.APPROVED).count())
                .rejectedCount(store.values().stream().filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count())
                .pendingCount(store.values().stream().filter(a -> a.getStatus() == ApplicationStatus.PENDING).count())
                .learnerLicenseCount(store.values().stream().filter(a -> a.getType() == ApplicationType.LL).count())
                .drivingLicenseCount(store.values().stream().filter(a -> a.getType() == ApplicationType.DL).count())
                .build();
    }

    private ApplicationReportDTO mapToDTO(Application app) {
        return ApplicationReportDTO.builder()
                .applicationNumber(app.getApplicationNumber())
                .applicantName(app.getApplicant() != null ? app.getApplicant().getFullName() : "N/A")
                .applicantEmail(app.getApplicant() != null ? app.getApplicant().getEmail() : "N/A")
                .applicantPhone(app.getApplicant() != null ? app.getApplicant().getPhone() : "N/A")
                .type(app.getType() != null ? app.getType().name() : "N/A")
                .status(app.getStatus() != null ? app.getStatus().name() : "N/A")
                .applicationDate(app.getApplicationDate())
                .testDate(app.getTestDate())
                .testResult(app.getTestResult() != null ? app.getTestResult() : "N/A")
                .amountPaid(app.getAmountPaid())
                .paymentStatus(app.getPaymentStatus())
                .build();
    }
}
