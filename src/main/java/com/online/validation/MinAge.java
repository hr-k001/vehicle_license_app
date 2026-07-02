package com.online.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for minimum age
 * US-017: Validate Applicant Data
 */
@Documented
@Constraint(validatedBy = MinAgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinAge {
    
    String message() default "Applicant must be at least 18 years old";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    int value() default 18;
}
