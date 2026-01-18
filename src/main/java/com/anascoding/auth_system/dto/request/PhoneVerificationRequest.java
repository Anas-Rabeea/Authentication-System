package com.anascoding.auth_system.dto.request;

import com.anascoding.auth_system.validator.EgyptianPhone.ValidEgyptianPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PhoneVerificationRequest (

        @ValidEgyptianPhone(message = "Enter a Valid Egyptian Number")
        @NotBlank(message = "Must Enter a Phone")
        String phone,

        @NotBlank(message = "Must Enter a Phone")
        @Size(max = 7 , message = "Not valid OTP")
        String otp
) {
}
