package com.anascoding.auth_system.validator.EgyptianPhone;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { ValidEgyptianPhoneValidator.class})
@Target({ METHOD, FIELD , CONSTRUCTOR, PARAMETER  })
@Retention(RUNTIME)
public @interface ValidEgyptianPhone {

    String message() default "Enter a valid Egyptian Phone number (11 numbers)";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
