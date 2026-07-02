package com.online.repository;

import com.online.model.DrivingLicense;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// US-016: View License Details
@Repository
@Primary
public interface LicenseRepository extends JpaRepository<DrivingLicense, Long> {

    Optional<DrivingLicense> findByDrivingLicenseNumber(String drivingLicenseNumber);

    Optional<DrivingLicense> findByApplicant_ApplicantId(Long applicantId);
}
