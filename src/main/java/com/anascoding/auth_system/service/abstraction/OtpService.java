package com.anascoding.auth_system.service.abstraction;

public interface OtpService {

    boolean verifyOtp(String otp);
    void sendPhoneVerificationOtp(String phone);
    String generateOTP(int tokenLength);

}
