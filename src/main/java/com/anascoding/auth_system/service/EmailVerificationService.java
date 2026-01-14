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
    private static final Duration token_ttl = Duration.ofMinutes(10);
    private final RedisTemplate<String,String> redisTemplate;
    private final EmailSenderService emailSenderService;

    @Async
    public void sendVerificationEmail(@Email @NotBlank String email) {


    }
}
