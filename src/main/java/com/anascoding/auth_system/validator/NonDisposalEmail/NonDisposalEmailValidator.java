package com.anascoding.auth_system.validator.NonDisposalEmail;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class NonDisposalEmailValidator implements ConstraintValidator<NonDisposalEmail, String> {

    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
            "mailinator.com",
            "10minutemail.com",
            "guerrillamail.com",
            "yopmail.com",
            "temp-mail.org"
    );

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true; // let @NotBlank handle it
        }
        // cut the domain part of the email
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        return !DISPOSABLE_DOMAINS.contains(domain);
    }
}
