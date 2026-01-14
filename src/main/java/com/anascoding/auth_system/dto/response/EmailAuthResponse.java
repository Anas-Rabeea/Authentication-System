package com.anascoding.auth_system.dto.response;


import lombok.Builder;

@Builder
public record EmailAuthResponse(
        String accessToken,
        String refreshToken
) {
}
