package com.online.service;

import com.online.dto.ApplicationReportDTO;
import com.online.dto.CountReportDTO;

import java.util.List;

/**
 * ReportService - Interface for Application Reporting
 * Provides methods to generate reports on applications
 * US-018: Application Reports
 */
public interface ReportService {

    /**
     * Get all applications
     * @return list of all applications as report DTOs
     */
    List<ApplicationReportDTO> getAllApplications();

    /**
     * Get approved applications
     * @return list of approved applications
     */
    List<ApplicationReportDTO> getApprovedApplications();

    /**
     * Get rejected applications
     * @return list of rejected applications
     */
    List<ApplicationReportDTO> getRejectedApplications();

    /**
     * Get pending applications
     * @return list of pending applications
     */
    List<ApplicationReportDTO> getPendingApplications();

    /**
     * Get count statistics for all applications
     * @return count report DTO with statistics
     */
    CountReportDTO getApplicationCounts();
}
