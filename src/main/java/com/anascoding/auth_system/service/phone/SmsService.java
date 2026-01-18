package com.anascoding.auth_system.service.phone;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableAsync
public class SmsService {

    private final TwilioProperties twilioProperties;

    @Async
    public void sendOtp(String phoneNumber, String otp) {
        Twilio.init(
                twilioProperties.getTWILIO_ACCOUNT_SID(),
                twilioProperties.getTWILIO_AUTH_TOKEN());
        // change later (make it more generic)
        String to =  "+2" + phoneNumber; // +2 is the EG Phone Code
        Message.creator(
                new com.twilio.type.PhoneNumber(to), // to
                new com.twilio.type.PhoneNumber(twilioProperties.getTWILIO_PHONE_NUMBER()), // from (Twilio)
                """
                        %s   is your OTP for Mehna.
                        Don't Share this code with anyone.
                        """.formatted(otp)
        ).create();
    }
}
