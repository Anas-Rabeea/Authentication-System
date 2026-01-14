package com.anascoding.auth_system.controller;


import com.anascoding.auth_system.dto.request.EmailAuthRequest;
import com.anascoding.auth_system.service.EmailAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    // one endpoint handles login/signup for using emails
    // EmailAuthResponse
    public ResponseEntity<?> emailAuthentication(
            @RequestBody @Valid
            EmailAuthRequest request)
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(emailAuthService.authenticate(request));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Can be accessed without authentication or registeration");
    }




}
