package com.anascoding.auth_system.validator.StrongPassword;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { StrongPasswordValidator.class})
public @interface StrongPassword {

    String message() default "Password MUST Follow Password Policy.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}