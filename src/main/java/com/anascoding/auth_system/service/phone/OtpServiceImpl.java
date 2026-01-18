package com.anascoding.auth_system.service.phone;


import com.anascoding.auth_system.entity.AppUser;
import com.anascoding.auth_system.repository.AppUserRepo;
import com.anascoding.auth_system.service.abstraction.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final AppUserRepo appUserRepo;
    private final SmsService smsService;
    private final RedisTemplate<String,String> redisTemplate;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Duration OTP_TTL = Duration.ofMinutes(2);


    @Override
    public void sendPhoneVerificationOtp(String phone)
    {
        String otp = this.generateOTP(7);
        // key = otp:phone:<Phone>
        // value = <Otp>
        redisTemplate
               .opsForValue()
               .set("otp:phone:%s".formatted(phone) , otp , OTP_TTL );
        this.smsService.sendOtp(phone , otp);
    }



    public boolean verifyOtp(String otp , String phone)
    {
        if(otp.isBlank())
            return false;

        String redisKey = "otp:phone:%s".formatted(phone);
        String storedOtp = redisTemplate
                            .opsForValue()
                            .get(redisKey);

        // if OTP is wrong redis will not return a value which means phone will be null
        if(storedOtp == null || !storedOtp.matches(otp))
            return false;

        // if reached this > means that phone is not null a
        AppUser verifiedUser =
                this.appUserRepo
                            .findByPhone(phone)
                            .orElseThrow(() -> new UsernameNotFoundException("Invalid Phone Number."));

        // TODO -- after user send correct OTP phoneVerified stays false in the DB ,  check why
        verifiedUser.setPhoneVerified(true);
        this.appUserRepo.save(verifiedUser);

        log.info("{} : Phone is verified." , phone);
        redisTemplate.delete(redisKey);

        return true;
    }

    public String generateOTP(int tokenLength)
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
