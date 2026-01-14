package com.anascoding.auth_system.validator.NonDisposalEmail;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {NonDisposalEmailValidator.class}
)
public @interface NonDisposalEmail {

    String message() default "Enter a NonDisposal Email (No Temporary Email Address).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
