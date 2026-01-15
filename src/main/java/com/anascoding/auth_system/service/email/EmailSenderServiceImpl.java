package com.anascoding.auth_system.service.email;

import com.anascoding.auth_system.service.abstraction.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Async(value = "emailThreadPoolTaskExecutor")
    @Override
    public void send(String to, String content, String title) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(title);
            message.setText(content);

            mailSender.send(message);

            log.info("Verification Email sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }

    }
}
