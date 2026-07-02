package com.online.controller;

import com.online.model.Applicant;
import com.online.service.ApplicantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// US-014: Manage Applicant Records
@RestController
@RequestMapping("/api/applicants")
public class ApplicantController {

    private final ApplicantService applicantService;

    public ApplicantController(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    @GetMapping
    public ResponseEntity<List<Applicant>> getAllApplicants() {
        return ResponseEntity.ok(applicantService.getAllApplicants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Applicant> getApplicantById(@PathVariable Long id) {
        return ResponseEntity.ok(applicantService.getApplicantById(id));
    }

    @PostMapping
    public ResponseEntity<Applicant> createApplicant(@Valid @RequestBody Applicant applicant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicantService.createApplicant(applicant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Applicant> updateApplicant(@PathVariable Long id, @Valid @RequestBody Applicant applicantDetails) {
        return ResponseEntity.ok(applicantService.updateApplicant(id, applicantDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteApplicant(@PathVariable Long id) {
        applicantService.deleteApplicant(id);
        return ResponseEntity.ok(Map.of("message", "Applicant with ID " + id + " deleted successfully"));
    }
}
