package com.anascoding.auth_system.controller;


import com.anascoding.auth_system.dto.request.EmailAuthRequest;
import com.anascoding.auth_system.dto.request.PhoneAuthRequest;
import com.anascoding.auth_system.dto.request.PhoneVerificationRequest;
import com.anascoding.auth_system.service.email.EmailAuthenticationService;
import com.anascoding.auth_system.service.email.EmailVerificationService;
import com.anascoding.auth_system.service.phone.OtpServiceImpl;
import com.anascoding.auth_system.service.phone.PhoneAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final EmailAuthenticationService emailAuthService;
    private final PhoneAuthenticationService phoneAuthService;
    private final EmailVerificationService emailVerificationService;
    private final OtpServiceImpl otpServiceImpl;



    // one endpoint handles login/signup for using emails
    @PostMapping("/email")
    public ResponseEntity<?> emailAuthentication(
            @RequestBody @Valid
            EmailAuthRequest request)
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(emailAuthService.authenticate(request));
    }


    // user will use this endpoint by clicking the link in there mail
    // URL Example : http://localhost:8055/api/v1/auth/verify-email?token=<token>
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        this.emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok("Email Verification is completed");
    }

    @PostMapping("/phone")
    public ResponseEntity<?> phoneAuthentication(
            @RequestBody @Valid
            PhoneAuthRequest request)
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(phoneAuthService.authenticate(request));
    }


    @PostMapping("/verify-phone")
    public ResponseEntity<String> verifyPhone(
            @RequestBody @Valid
            PhoneVerificationRequest request
    ){
        this.otpServiceImpl.verifyOtp(request.otp() , request.phone());
        return ResponseEntity.ok("Phone Verification is completed , Now you can login using your phone number");
    }


    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Can be accessed without authentication or registeration");
    }
}
