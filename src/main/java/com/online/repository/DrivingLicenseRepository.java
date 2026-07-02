package com.online.repository;

import org.springframework.stereotype.Repository;

/**
 * DrivingLicenseRepository - Data access layer for DrivingLicense entity
 * Uses JpaRepository for CRUD operations
 * US-015: Generate Unique License Number
 */
@Repository
public interface DrivingLicenseRepository extends LicenseRepository {
}
