package com.anascoding.auth_system.validator.IPv4;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Ipv4AddressValidator implements ConstraintValidator<IpV4Address, String> {

    // IPv4 regex pattern
    private static final String IPV4_PATTERN =
            "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}" +
                    "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.matches(IPV4_PATTERN);
    }
}
