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

        @Email(message = "Enter a valid email address.")
        @NotBlank(message = "Email cant be empty.")
        @NonDisposalEmail
        String email,

        @NotBlank(message = "Password cant be empty.")
//        @Min(value = 10, message = "Password Should be at least 10")
//        @Max(value = 100, message = "Password Should be at most 100")
//        @StrongPassword
        String password,

        @Enumerated(EnumType.STRING)
        String role
) {
}
