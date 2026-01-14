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


        String token =  verificationTokenService.generateEmailVerificationToken(10);
        // Token_2RI#@L : To not make token guessing easy (prevents brute-force)
        String redisKey = "Token_2RI#@L:" + token;
        // save  the token with email in redis
        redisTemplate
                .opsForValue()
                .set(redisKey,email ,token_ttl);

        String verificationLink = "http://localhost:8055/api/v1/auth/verify-email?token=" + token;
        // send the email
        String verificationEmailContent = """
                    Hello %s,
                    Please verify your account by clicking the link below:
                    %s
                    NOTE: This link expires in 3 minutes.
                    If you did not make this request, please disregard this email.
                """.formatted(email,verificationLink) ;
        String verificationEmailTitle = "Your Verification Code For Mehna";
        this.emailSenderService.send(
                email,
                verificationEmailContent,
                verificationEmailTitle
        );
    }
}
