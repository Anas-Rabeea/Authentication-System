package com.anascoding.auth_system.dto.request;

import com.anascoding.auth_system.validator.NonDisposalEmail.NonDisposalEmail;
import com.anascoding.auth_system.validator.StrongPassword.StrongPassword;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record EmailAuthRequest(

        @Email(message = "Enter a valid email address.")
        @NotBlank(message = "Email cant be empty.")
        @Size(max = 100 , message = "email length must be between 11 and 99")
        @NonDisposalEmail
        String email,

        @NotBlank(message = "Password cant be empty.")
        @Size(min = 10,max = 100 , message = "Password length must be between 11 and 99")
        @StrongPassword
        String password,

        @Enumerated(EnumType.STRING)
        String role
) {
}
