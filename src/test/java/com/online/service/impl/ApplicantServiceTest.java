package com.online.service.impl;

import com.online.exception.ApplicantNotFoundException;
import com.online.model.Applicant;
import com.online.repository.ApplicantRepository;
import com.online.service.ApplicantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Applicant Service Tests — US-014")
class ApplicantServiceTest {

    @Mock private ApplicantRepository applicantRepository;
    private ApplicantService applicantService;
    private Applicant testApplicant;

    @BeforeEach
    void setUp() {
        applicantService = new ApplicantServiceImpl(applicantRepository);
        testApplicant = Applicant.builder()
                .applicantId(1L).fullName("John Doe").email("john@example.com")
                .phone("9876543210").address("123 Main Street").aadhaarNumber("123456789012")
                .dateOfBirth(LocalDate.of(2000, 1, 15)).build();
    }

    @Test @DisplayName("Should return all applicants")
    void testGetAllApplicants_Success() {
        when(applicantRepository.findAll()).thenReturn(Arrays.asList(testApplicant));
        List<Applicant> result = applicantService.getAllApplicants();
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        verify(applicantRepository).findAll();
    }

    @Test @DisplayName("Should return empty list when no applicants")
    void testGetAllApplicants_Empty() {
        when(applicantRepository.findAll()).thenReturn(Arrays.asList());
        assertEquals(0, applicantService.getAllApplicants().size());
    }

    @Test @DisplayName("Should get applicant by ID")
    void testGetApplicantById_Success() {
        when(applicantRepository.findById(1L)).thenReturn(Optional.of(testApplicant));
        Applicant result = applicantService.getApplicantById(1L);
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        verify(applicantRepository).findById(1L);
    }

    @Test @DisplayName("Should throw exception for unknown ID")
    void testGetApplicantById_NotFound() {
        when(applicantRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ApplicantNotFoundException.class, () -> applicantService.getApplicantById(999L));
    }

    @Test @DisplayName("Should create applicant")
    void testCreateApplicant_Success() {
        when(applicantRepository.save(any())).thenReturn(testApplicant);
        Applicant result = applicantService.createApplicant(testApplicant);
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        verify(applicantRepository).save(any());
    }

    @Test @DisplayName("Should update applicant")
    void testUpdateApplicant_Success() {
        Applicant updated = Applicant.builder().fullName("John Updated").email("new@example.com")
                .phone("9999999999").address("New Address").aadhaarNumber("123456789012")
                .dateOfBirth(LocalDate.of(2000, 1, 15)).build();
        when(applicantRepository.findById(1L)).thenReturn(Optional.of(testApplicant));
        when(applicantRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Applicant result = applicantService.updateApplicant(1L, updated);
        assertEquals("John Updated", result.getFullName());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test @DisplayName("Should throw exception when updating non-existent applicant")
    void testUpdateApplicant_NotFound() {
        when(applicantRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ApplicantNotFoundException.class, () ->
                applicantService.updateApplicant(999L, testApplicant));
    }

    @Test @DisplayName("Should delete applicant")
    void testDeleteApplicant_Success() {
        when(applicantRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> applicantService.deleteApplicant(1L));
        verify(applicantRepository).deleteById(1L);
    }

    @Test @DisplayName("Should throw exception when deleting non-existent applicant")
    void testDeleteApplicant_NotFound() {
        when(applicantRepository.existsById(999L)).thenReturn(false);
        assertThrows(ApplicantNotFoundException.class, () -> applicantService.deleteApplicant(999L));
        verify(applicantRepository, never()).deleteById(any());
    }
}
