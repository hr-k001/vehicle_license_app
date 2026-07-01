package com.online.controller;

import com.online.model.Application;
import com.online.model.ApplicationStatus;
import com.online.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

// US-003, US-004 (Mansidak) | US-007, US-008, US-009 (Himanshu)
@RestController
@RequestMapping("/api/license")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    // US-003
    @PostMapping("/ll/apply")
    public ResponseEntity<Map<String, String>> applyForLL(@RequestBody Application application) {
        String message = licenseService.applyForLL(application);
        if ("Invalid application details".equals(message)) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }
        return ResponseEntity.ok(Map.of("message", message, "applicationNumber", application.getApplicationNumber()));
    }

    // US-004
    @GetMapping("/ll/status/{applicationNumber}")
    public ResponseEntity<Map<String, String>> viewLLStatus(@PathVariable String applicationNumber) {
        ApplicationStatus status = licenseService.viewLLStatus(applicationNumber);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("applicationNumber", applicationNumber, "status", status.name()));
    }

    // US-007
    @PostMapping("/dl/apply")
    public ResponseEntity<Map<String, String>> applyForDL(@RequestBody Application application) {
        String message = licenseService.applyForDL(application);
        if ("Invalid application details".equals(message)) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }
        return ResponseEntity.ok(Map.of("message", message, "applicationNumber", application.getApplicationNumber()));
    }

    // US-008
    @PutMapping("/dl/{applicationNumber}/schedule-test")
    public ResponseEntity<Map<String, String>> scheduleDrivingTest(
            @PathVariable String applicationNumber,
            @RequestBody Map<String, String> body) {
        String message = licenseService.scheduleDrivingTest(applicationNumber, new Date());
        if (message.contains("not found") || message.contains("Invalid")) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }
        return ResponseEntity.ok(Map.of("message", message));
    }

    // US-009
    @GetMapping("/dl/status/{applicationNumber}")
    public ResponseEntity<Map<String, String>> viewDLStatus(@PathVariable String applicationNumber) {
        ApplicationStatus status = licenseService.viewDLStatus(applicationNumber);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("applicationNumber", applicationNumber, "status", status.name()));
    }

    // Check LL approval status by email (used before DL submission)
    @GetMapping("/ll/check-by-email")
    public ResponseEntity<Map<String, String>> checkLLByEmail(@RequestParam String email) {
        Application app = licenseService.getLLApplicationByEmail(email);
        if (app == null) {
            return ResponseEntity.ok(Map.of("status", "NONE"));
        }
        return ResponseEntity.ok(Map.of(
            "status", app.getStatus().name(),
            "applicationNumber", app.getApplicationNumber()
        ));
    }
}
