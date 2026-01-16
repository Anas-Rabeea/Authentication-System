package com.anascoding.auth_system.dto.response;

import lombok.Builder;

@Builder
public record PhoneAuthResponse(
        String accessToken,
        String refreshToken
) {
}
