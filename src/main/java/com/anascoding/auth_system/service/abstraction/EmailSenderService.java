package com.anascoding.auth_system.service.abstraction;

public interface EmailSenderService {
    void send(String to , String content , String title);
}
