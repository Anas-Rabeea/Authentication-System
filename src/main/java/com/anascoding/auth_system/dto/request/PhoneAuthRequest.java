package com.anascoding.auth_system.dto.request;

import com.anascoding.auth_system.validator.EgyptianPhone.ValidEgyptianPhone;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PhoneAuthRequest(

        @NotBlank
        @ValidEgyptianPhone
        String phone,

        @Enumerated(EnumType.STRING)
        String role

        )
{}
