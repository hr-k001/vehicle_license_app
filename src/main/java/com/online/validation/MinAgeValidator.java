package com.online.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

/**
 * MinAgeValidator - Validates that an applicant meets the minimum age requirement
 * US-017: Validate Applicant Data
 */
public class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {

    private int minAge;

    @Override
    public void initialize(MinAge annotation) {
        this.minAge = annotation.value();
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) return false;
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= minAge;
    }
}
