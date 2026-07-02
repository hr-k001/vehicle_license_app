package com.online.controller;

import com.online.model.Application;
import com.online.service.RTOOfficerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// US-005 (Mansidak) | US-006, US-010, US-011, US-012, US-013 (Himanshu)
@RestController
@RequestMapping("/api/rto")
public class RTOOfficerController {

    private final RTOOfficerService rtoOfficerService;

    public RTOOfficerController(RTOOfficerService rtoOfficerService) {
        this.rtoOfficerService = rtoOfficerService;
    }

    // US-005
    @PutMapping("/ll/approve/{applicationNumber}")
    public ResponseEntity<Map<String, String>> approveLearnerLicense(@PathVariable String applicationNumber) {
        String message = rtoOfficerService.approveLearnerLicense(applicationNumber);
        if ("Application not found".equals(message)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", message));
    }

    // US-006
    @PutMapping("/ll/reject/{applicationNumber}")
    public ResponseEntity<Map<String, String>> rejectLearnerLicense(@PathVariable String applicationNumber) {
        String message = rtoOfficerService.rejectLearnerLicense(applicationNumber);
        if ("Application not found".equals(message)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", message));
    }

    // US-010
    @PutMapping("/dl/approve/{applicationNumber}")
    public ResponseEntity<Map<String, String>> approveDrivingLicense(@PathVariable String applicationNumber) {
        String message = rtoOfficerService.approveDrivingLicense(applicationNumber);
        if ("Application not found".equals(message)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", message));
    }

    // US-011
    @PutMapping("/dl/reject/{applicationNumber}")
    public ResponseEntity<Map<String, String>> rejectDrivingLicense(@PathVariable String applicationNumber) {
        String message = rtoOfficerService.rejectDrivingLicense(applicationNumber);
        if ("Application not found".equals(message)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", message));
    }

    // US-012
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(rtoOfficerService.getAllApplications());
    }

    // US-013
    @GetMapping("/applications/search")
    public ResponseEntity<List<Application>> searchApplications(@RequestParam String q) {
        return ResponseEntity.ok(rtoOfficerService.searchApplications(q));
    }

    @PutMapping("/applications/{applicationNumber}")
    public ResponseEntity<Map<String, String>> updateApplicationDetails(@PathVariable String applicationNumber,
                                                                       @RequestBody Application updatedApplication) {
        String message = rtoOfficerService.updateApplicationDetails(applicationNumber, updatedApplication);
        if ("Application not found".equals(message)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PutMapping("/test/pass/{applicationNumber}")
    public ResponseEntity<Map<String, String>> passTest(@PathVariable String applicationNumber) {
        String message = rtoOfficerService.passTest(applicationNumber);
        if ("Application not found".equals(message)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PutMapping("/test/fail/{applicationNumber}")
    public ResponseEntity<Map<String, String>> failTest(@PathVariable String applicationNumber) {
        String message = rtoOfficerService.failTest(applicationNumber);
        if ("Application not found".equals(message)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", message));
    }
}
