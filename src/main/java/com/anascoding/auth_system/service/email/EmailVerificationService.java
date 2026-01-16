package com.anascoding.auth_system.service.email;

import com.anascoding.auth_system.entity.AppUser;
import com.anascoding.auth_system.repository.AppUserRepo;
import com.anascoding.auth_system.service.abstraction.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

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
        // Token_2RI#@L : To not make token guessing easy
        String redisKey = "email-verification:Token_2RI#@L:" + token;
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
                """.formatted(email.substring( 0 , email.indexOf("@") ) , verificationLink) ;
        String verificationEmailTitle = "Your Verification Link For Mehna";
        emailSenderService.send(
                email,
                verificationEmailContent,
                verificationEmailTitle
        );


    }

    // token is sent to the user so this method will verify the incoming token
    // from the link the user clicked on it his mail
    public void verifyEmail(String token){

        String redisKey = "email-verification:Token_2RI#@L:" + token;
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
        log.info( email + " is verified at " + LocalDateTime.now());
        this.userRepo.save(user); // to update the new registered user email verification state
        redisTemplate.delete(redisKey);
    }

}
