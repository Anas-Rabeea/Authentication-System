package com.anascoding.auth_system.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ErrorResponse {

    private HttpStatus status;
}
