package com.anascoding.auth_system.controller;


import com.anascoding.auth_system.dto.request.EmailAuthRequest;
import com.anascoding.auth_system.service.EmailAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final EmailAuthenticationService emailAuthService;

    @PostMapping("/email")
    public ResponseEntity<?> emailAuthentication(
            @RequestBody @Valid
            EmailAuthRequest request)
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(emailAuthService.authenticate(request));
    }




}
