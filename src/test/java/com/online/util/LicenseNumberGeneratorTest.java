package com.online.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LicenseNumberGeneratorTest - Unit tests for LicenseNumberGenerator
 * Tests unique license number generation with format: DL-YYYY-000001
 * Uses JUnit 5 for testing
 * Target: 90%+ code coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("License Number Generator Tests")
class LicenseNumberGeneratorTest {

    private LicenseNumberGenerator licenseNumberGenerator;
    private static final String LICENSE_NUMBER_PATTERN = "^DL-\\d{4}-\\d{6}$";

    @BeforeEach
    void setUp() {
        licenseNumberGenerator = new LicenseNumberGenerator();
    }

    // ============ BASIC GENERATION TESTS ============

    @Test
    @DisplayName("Should generate license number with correct format")
    void testGenerateLicenseNumber_CorrectFormat() {
        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();

        // Assert
        assertNotNull(licenseNumber);
        assertTrue(licenseNumber.matches(LICENSE_NUMBER_PATTERN),
                "License number should match pattern DL-YYYY-000001");
        assertTrue(licenseNumber.startsWith("DL-"));
        assertTrue(licenseNumber.contains(String.valueOf(Year.now().getValue())));
    }

    @Test
    @DisplayName("Should generate license number starting with DL-")
    void testGenerateLicenseNumber_StartsWithDL() {
        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();

        // Assert
        assertTrue(licenseNumber.startsWith("DL-"));
    }

    @Test
    @DisplayName("Should include current year in license number")
    void testGenerateLicenseNumber_IncludesYear() {
        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();

        // Assert
        int currentYear = Year.now().getValue();
        assertTrue(licenseNumber.contains(String.valueOf(currentYear)));
    }

    @Test
    @DisplayName("Should have 6-digit sequence number")
    void testGenerateLicenseNumber_SixDigitSequence() {
        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();

        // Assert
        String sequence = licenseNumber.substring(licenseNumber.lastIndexOf("-") + 1);
        assertEquals(6, sequence.length());
        assertTrue(sequence.matches("\\d{6}"));
    }

    // ============ UNIQUENESS TESTS ============

    @Test
    @DisplayName("Should generate unique license numbers")
    void testGenerateLicenseNumber_Uniqueness() {
        // Arrange
        Set<String> generatedNumbers = new HashSet<>();
        int iterations = 100;

        // Act
        for (int i = 0; i < iterations; i++) {
            String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
            generatedNumbers.add(licenseNumber);
        }

        // Assert
        assertEquals(iterations, generatedNumbers.size(),
                "All generated license numbers should be unique");
    }

    @Test
    @DisplayName("Should increment sequence for consecutive calls")
    void testGenerateLicenseNumber_IncrementingSequence() {
        // Act
        String first = licenseNumberGenerator.generateLicenseNumber();
        String second = licenseNumberGenerator.generateLicenseNumber();
        String third = licenseNumberGenerator.generateLicenseNumber();

        // Extract sequences
        long firstSeq = Long.parseLong(first.substring(first.lastIndexOf("-") + 1));
        long secondSeq = Long.parseLong(second.substring(second.lastIndexOf("-") + 1));
        long thirdSeq = Long.parseLong(third.substring(third.lastIndexOf("-") + 1));

        // Assert
        assertEquals(firstSeq + 1, secondSeq);
        assertEquals(secondSeq + 1, thirdSeq);
    }

    @Test
    @DisplayName("Should generate 1000 unique license numbers without collision")
    void testGenerateLicenseNumber_LargeDataSet() {
        // Arrange
        Set<String> generatedNumbers = new HashSet<>();

        // Act
        for (int i = 0; i < 1000; i++) {
            String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
            generatedNumbers.add(licenseNumber);
        }

        // Assert
        assertEquals(1000, generatedNumbers.size());
        assertTrue(generatedNumbers.stream().allMatch(ln -> ln.matches(LICENSE_NUMBER_PATTERN)));
    }

    @Test
    @DisplayName("Should not exceed 999999 sequence limit")
    void testGenerateLicenseNumber_SequenceNotExceeded() {
        // Act
        long currentSequence = licenseNumberGenerator.getCurrentSequenceForYear(Year.now().getValue());

        // Assert
        assertTrue(currentSequence <= 999999,
                "Sequence number should not exceed 999999");
    }

    // ============ SEQUENCE TRACKING TESTS ============

    @Test
    @DisplayName("Should track current sequence for year")
    void testGetCurrentSequenceForYear_Track() {
        // Act
        licenseNumberGenerator.generateLicenseNumber();
        licenseNumberGenerator.generateLicenseNumber();
        long sequence = licenseNumberGenerator.getCurrentSequenceForYear(Year.now().getValue());

        // Assert
        assertEquals(2L, sequence);
    }

    @Test
    @DisplayName("Should return 0 for new year without generated licenses")
    void testGetCurrentSequenceForYear_NewYear() {
        // Act
        long sequence = licenseNumberGenerator.getCurrentSequenceForYear(2099);

        // Assert
        assertEquals(0L, sequence);
    }

    // ============ RESET FUNCTIONALITY TESTS ============

    @Test
    @DisplayName("Should reset counter for specific year")
    void testResetCounterForYear_Success() {
        // Arrange
        licenseNumberGenerator.generateLicenseNumber();
        licenseNumberGenerator.generateLicenseNumber();
        int currentYear = Year.now().getValue();

        // Act
        licenseNumberGenerator.resetCounterForYear(currentYear);
        long sequence = licenseNumberGenerator.getCurrentSequenceForYear(currentYear);

        // Assert
        assertEquals(0L, sequence);
    }

    @Test
    @DisplayName("Should generate starting from 1 after reset")
    void testGenerateLicenseNumber_AfterReset() {
        // Arrange
        licenseNumberGenerator.generateLicenseNumber();
        licenseNumberGenerator.generateLicenseNumber();
        int currentYear = Year.now().getValue();
        licenseNumberGenerator.resetCounterForYear(currentYear);

        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
        String sequence = licenseNumber.substring(licenseNumber.lastIndexOf("-") + 1);

        // Assert
        assertEquals("000001", sequence);
    }

    // ============ THREAD SAFETY TESTS ============

    @Test
    @DisplayName("Should generate license numbers in multi-threaded environment")
    void testGenerateLicenseNumber_ThreadSafe() throws InterruptedException {
        // Arrange
        Set<String> generatedNumbers = new HashSet<>();
        int threadCount = 10;
        int numbersPerThread = 100;
        Thread[] threads = new Thread[threadCount];

        // Act
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < numbersPerThread; j++) {
                    String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
                    synchronized (generatedNumbers) {
                        generatedNumbers.add(licenseNumber);
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(threadCount * numbersPerThread, generatedNumbers.size(),
                "All numbers generated in multi-threaded environment should be unique");
    }

    // ============ FORMAT VALIDATION TESTS ============

    @Test
    @DisplayName("Should validate license number format with regex")
    void testGenerateLicenseNumber_RegexValidation() {
        // Arrange
        Pattern pattern = Pattern.compile(LICENSE_NUMBER_PATTERN);

        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
        boolean matches = pattern.matcher(licenseNumber).matches();

        // Assert
        assertTrue(matches);
    }

    @Test
    @DisplayName("Should format sequence with leading zeros")
    void testGenerateLicenseNumber_LeadingZeros() {
        // Arrange
        int currentYear = Year.now().getValue();
        licenseNumberGenerator.resetCounterForYear(currentYear);

        // Act
        String first = licenseNumberGenerator.generateLicenseNumber();
        String second = licenseNumberGenerator.generateLicenseNumber();

        // Assert
        assertTrue(first.endsWith("000001"));
        assertTrue(second.endsWith("000002"));
    }

    // ============ YEAR SEPARATION TESTS ============

    @Test
    @DisplayName("Should maintain separate counters for different years")
    void testGenerateLicenseNumber_YearSeparation() {
        // Arrange & Act
        licenseNumberGenerator.generateLicenseNumber();
        long sequence2026 = licenseNumberGenerator.getCurrentSequenceForYear(2026);
        long sequence2027 = licenseNumberGenerator.getCurrentSequenceForYear(2027);

        // Assert
        assertEquals(1L, sequence2026);
        assertEquals(0L, sequence2027);
    }

    // ============ EDGE CASE TESTS ============

    @Test
    @DisplayName("Should handle repeated reset and generate")
    void testGenerateLicenseNumber_RepeatedResetAndGenerate() {
        // Act & Assert
        for (int i = 1; i <= 5; i++) {
            licenseNumberGenerator.resetCounterForYear(Year.now().getValue());
            String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
            assertTrue(licenseNumber.endsWith("000001"));
        }
    }

    @Test
    @DisplayName("Should verify license number components")
    void testGenerateLicenseNumber_ComponentVerification() {
        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
        String[] components = licenseNumber.split("-");

        // Assert
        assertEquals(3, components.length);
        assertEquals("DL", components[0]);
        assertEquals(4, components[1].length());
        assertEquals(6, components[2].length());
    }

    @Test
    @DisplayName("Should generate numeric year in license number")
    void testGenerateLicenseNumber_NumericYear() {
        // Act
        String licenseNumber = licenseNumberGenerator.generateLicenseNumber();
        String year = licenseNumber.split("-")[1];

        // Assert
        assertTrue(year.matches("\\d{4}"));
        int yearValue = Integer.parseInt(year);
        assertTrue(yearValue >= 2020 && yearValue <= 2099);
    }
}
