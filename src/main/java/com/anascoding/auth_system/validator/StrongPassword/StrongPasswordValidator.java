package com.anascoding.auth_system.validator.StrongPassword;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
// annotation Type and what is the value type which will be evaluated
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword,String> {

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])[^\\s]{10,}$"; // min 10 chars, no spaces

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // make @NotNull and @NotBlank handle the NULL Values
        if (value == null){
            return true;
        }
        return value.matches(PASSWORD_REGEX);

    }
}
