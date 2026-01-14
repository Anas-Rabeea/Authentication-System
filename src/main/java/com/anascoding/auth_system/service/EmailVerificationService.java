package com.anascoding.auth_system.service;

import com.anascoding.auth_system.entity.AppUser;
import com.anascoding.auth_system.repository.AppUserRepo;
import com.anascoding.auth_system.service.abstraction.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    // TODO -- set isEmailVerified to true after verification is completed

    // Key = token , Value = email
    // because user can send 2 verifications in a short period of time
    // so we make token which will be unique as the key
    private static final Duration token_ttl = Duration.ofMinutes(3);

    private final RedisTemplate<String,String> redisTemplate;
    private final EmailSenderService emailSenderService;
    private final VerificationTokenService verificationTokenService;
    private final AppUserRepo userRepo;


    public void sendVerificationEmail(String email) {

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
        log.info("Sent email verification to" + email);

    }

    // token is sent to the user so this method will verify the incoming token
    // from the link the user clicked on it his mail
    public void verifyEmail(String token){

        String redisKey = "Token_2RI#@L:" + token;
        String email = redisTemplate
                            .opsForValue()
                            .get(redisKey);
        // if the given token = token in redis > verify isEmailVerified = true else = false and exception
        if ( email == null)
            throw new RuntimeException("Invalid Verification Token.");

        AppUser user =  this.userRepo
                            .findByEmail(email)
                            .orElseThrow(() -> new UsernameNotFoundException("Email not found."));
        user.setEmailVerified(true);
        log.info("%s is verified." + email);
        this.userRepo.save(user); // to update the new registered user email verification state
        redisTemplate.delete(redisKey);
    }

}
