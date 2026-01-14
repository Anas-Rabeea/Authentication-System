package com.anascoding.auth_system.service;


import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class VerificationTokenService {

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:'\",.<>?/";

    public String generateEmailVerificationToken(int tokenLength)
    {
        Random random = new SecureRandom();

        StringBuilder sb =
                new StringBuilder(tokenLength);
        int charSetLength = CHAR_SET.length();  // to get a random index
        for (int i = 0 ; i < tokenLength ; i++){
            int charToAddIndex = random.nextInt(charSetLength);
            sb.append(CHAR_SET.charAt(charToAddIndex)) ;
        }
        return sb.toString();
    }


}
