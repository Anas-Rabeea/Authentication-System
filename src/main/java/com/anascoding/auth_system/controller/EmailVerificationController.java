package com.anascoding.auth_system.controller;

import com.anascoding.auth_system.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {

    private final EmailVerificationService service;

    @Autowired
    public EmailVerificationController(EmailVerificationService service) {
        this.service = service;
    }

    // user will use this endpoint by clicking the link in there mail
    // URL Example : http://localhost:8055/api/v1/auth/verify-email?token=<token>
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        this.service.verifyEmail(token);
        return ResponseEntity.ok("Email Verification is completed");
    }



}
