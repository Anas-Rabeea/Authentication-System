package com.anascoding.auth_system.validator.EgyptianPhone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEgyptianPhoneValidator implements ConstraintValidator<ValidEgyptianPhone,String> {

    // example : 01234567891
    private static final String PHONE_REGEX = "^01[0125][0-9]{8}$";;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // make @NotNull and @NotBlank handle the NULL Values
        if (value == null || value.isBlank()){
            return true;
        }
        return value.matches(PHONE_REGEX);

    }
}
