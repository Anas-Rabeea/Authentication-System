package com.anascoding.auth_system.service.abstraction;

public interface OtpService {

    boolean verifyOtp(String otp , String incomingPhone);
    void sendPhoneVerificationOtp(String phone);
    String generateOTP(int tokenLength);

}
