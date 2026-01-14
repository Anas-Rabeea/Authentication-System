package com.anascoding.auth_system.dto.request;

import com.anascoding.auth_system.validator.NonDisposalEmail.NonDisposalEmail;
import com.anascoding.auth_system.validator.StrongPassword.StrongPassword;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailAuthRequest(

        @Email @NotBlank
        @NonDisposalEmail
        String email,

        @Min(10) @Max(100)
        @NotBlank @StrongPassword
        String password,
        @Enumerated(EnumType.STRING)
        String role
) {
}
