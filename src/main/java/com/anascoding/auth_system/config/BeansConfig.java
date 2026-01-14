package com.anascoding.auth_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class BeansConfig {

    @Bean
    public MailSender mailsender(){
        return new JavaMailSenderImpl();
    }
}
