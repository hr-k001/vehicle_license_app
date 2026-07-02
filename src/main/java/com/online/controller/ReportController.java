package com.online.controller;

import com.online.dto.ApplicationReportDTO;
import com.online.dto.CountReportDTO;
import com.online.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// US-018: Application Reports
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ApplicationReportDTO>> getAllApplications() {
        return ResponseEntity.ok(reportService.getAllApplications());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ApplicationReportDTO>> getApproved() {
        return ResponseEntity.ok(reportService.getApprovedApplications());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<ApplicationReportDTO>> getRejected() {
        return ResponseEntity.ok(reportService.getRejectedApplications());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ApplicationReportDTO>> getPending() {
        return ResponseEntity.ok(reportService.getPendingApplications());
    }

    @GetMapping("/counts")
    public ResponseEntity<CountReportDTO> getCounts() {
        return ResponseEntity.ok(reportService.getApplicationCounts());
    }
}
