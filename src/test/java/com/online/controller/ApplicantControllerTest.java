package com.online.controller;

import com.online.exception.ApplicantNotFoundException;
import com.online.model.Applicant;
import com.online.service.ApplicantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Applicant Controller Tests — US-014")
class ApplicantControllerTest {

    @Mock private ApplicantService applicantService;
    private ApplicantController applicantController;
    private Applicant testApplicant;

    @BeforeEach
    void setUp() {
        applicantController = new ApplicantController(applicantService);
        testApplicant = Applicant.builder().applicantId(1L).fullName("John Doe")
                .email("john@example.com").phone("9876543210").address("123 Main Street")
                .aadhaarNumber("123456789012").dateOfBirth(LocalDate.of(2000, 1, 15)).build();
    }

    @Test @DisplayName("Should return all applicants with 200")
    void testGetAllApplicants_Success() {
        when(applicantService.getAllApplicants()).thenReturn(Arrays.asList(testApplicant));
        ResponseEntity<List<Applicant>> response = applicantController.getAllApplicants();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getFullName());
    }

    @Test @DisplayName("Should return empty list with 200")
    void testGetAllApplicants_Empty() {
        when(applicantService.getAllApplicants()).thenReturn(Arrays.asList());
        ResponseEntity<List<Applicant>> response = applicantController.getAllApplicants();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test @DisplayName("Should get applicant by ID with 200")
    void testGetApplicantById_Success() {
        when(applicantService.getApplicantById(1L)).thenReturn(testApplicant);
        ResponseEntity<Applicant> response = applicantController.getApplicantById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getFullName());
    }

    @Test @DisplayName("Should throw exception for unknown ID")
    void testGetApplicantById_NotFound() {
        when(applicantService.getApplicantById(999L)).thenThrow(new ApplicantNotFoundException(999L));
        assertThrows(ApplicantNotFoundException.class, () -> applicantController.getApplicantById(999L));
    }

    @Test @DisplayName("Should create applicant with 201")
    void testCreateApplicant_Success() {
        Applicant saved = Applicant.builder().applicantId(2L).fullName("Alice")
                .email("alice@example.com").phone("9876543212").address("456 Oak Ave")
                .aadhaarNumber("123456789014").dateOfBirth(LocalDate.of(2001, 8, 10)).build();
        when(applicantService.createApplicant(any())).thenReturn(saved);
        ResponseEntity<Applicant> response = applicantController.createApplicant(testApplicant);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Alice", response.getBody().getFullName());
    }

    @Test @DisplayName("Should update applicant with 200")
    void testUpdateApplicant_Success() {
        Applicant updated = Applicant.builder().applicantId(1L).fullName("John Updated")
                .email("new@example.com").phone("9999999999").address("New Addr")
                .aadhaarNumber("123456789012").dateOfBirth(LocalDate.of(2000, 1, 15)).build();
        when(applicantService.updateApplicant(1L, testApplicant)).thenReturn(updated);
        ResponseEntity<Applicant> response = applicantController.updateApplicant(1L, testApplicant);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Updated", response.getBody().getFullName());
    }

    @Test @DisplayName("Should throw exception when updating non-existent applicant")
    void testUpdateApplicant_NotFound() {
        when(applicantService.updateApplicant(999L, testApplicant)).thenThrow(new ApplicantNotFoundException(999L));
        assertThrows(ApplicantNotFoundException.class, () -> applicantController.updateApplicant(999L, testApplicant));
    }

    @Test @DisplayName("Should delete applicant with 200")
    void testDeleteApplicant_Success() {
        doNothing().when(applicantService).deleteApplicant(1L);
        ResponseEntity<java.util.Map<String, String>> response = applicantController.deleteApplicant(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("deleted successfully"));
    }

    @Test @DisplayName("Should throw exception when deleting non-existent applicant")
    void testDeleteApplicant_NotFound() {
        doThrow(new ApplicantNotFoundException(999L)).when(applicantService).deleteApplicant(999L);
        assertThrows(ApplicantNotFoundException.class, () -> applicantController.deleteApplicant(999L));
    }
}
