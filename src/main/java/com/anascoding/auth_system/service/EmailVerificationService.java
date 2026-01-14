package com.anascoding.auth_system.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    // Key = Email , Value = Token

    @Async
    public void sendVerificationEmail(@Email @NotBlank String email) {


    }
}
