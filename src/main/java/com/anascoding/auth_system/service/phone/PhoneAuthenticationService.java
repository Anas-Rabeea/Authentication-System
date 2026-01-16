package com.anascoding.auth_system.service.phone;


import com.anascoding.auth_system.dto.request.PhoneAuthRequest;
import com.anascoding.auth_system.dto.response.PhoneAuthResponse;
import com.anascoding.auth_system.entity.AppAuthProvider;
import com.anascoding.auth_system.entity.AppUser;
import com.anascoding.auth_system.entity.Role;
import com.anascoding.auth_system.repository.AppUserRepo;
import com.anascoding.auth_system.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneAuthenticationService {


        private final OtpServiceImpl otpServiceImpl;
        private final AppUserRepo appUserRepo;
        private final AuthenticationManager authManager;
        private final JwtUtils jwtUtils;

        public PhoneAuthResponse authenticate(PhoneAuthRequest request)
        {
            Optional<AppUser> userFromDb = appUserRepo
                    .findByPhone(request.phone());

            if(userFromDb.isEmpty()){
                AppUser newUser =
                        registerNewUser(request);
                this.otpServiceImpl.sendPhoneVerificationOtp(request.phone());
            }

            AppUser user = userFromDb.get();

            // Check if email is verified (Some users add email and password and wait till email verification)
            if(!user.isEmailVerified()){
                this.otpServiceImpl.sendPhoneVerificationOtp(user.getPhone());
            }
            // if it reached till here means that user is already existed (Login)
            authenticateUser(user);
            return generateAccessToken(user);
        }


    private Role chooseRole(String role){
        // customer > is the one who offers jobs to workers
        // worker is like electrician, plumber
        return role.matches("Customer") ? Role.CUSTOMER : Role.WORKER;
    }

    private AppUser registerNewUser(PhoneAuthRequest request){
        final AppUser newAppUser =
                AppUser
                        .builder()
                        .phone(request.phone())
                        .appAuthProvider(AppAuthProvider.LOCAL)
                        .phoneVerified(false)
                        .role(this.chooseRole(request.role()))
                        .build();
        return appUserRepo.save(newAppUser);
    }

    private void authenticateUser(AppUser user) {


        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user.getPhone(),
                        null,
                        user.getAuthorities()
                ) ;
        authManager.authenticate(authToken);
    }


    private PhoneAuthResponse generateAccessToken(AppUser userFromDb) {
        final String accessToken = this.jwtUtils.generateAccessToken(userFromDb.getPhone());
        final String refreshToken = this.jwtUtils.generateRefreshToken(userFromDb.getPhone());

        return PhoneAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
