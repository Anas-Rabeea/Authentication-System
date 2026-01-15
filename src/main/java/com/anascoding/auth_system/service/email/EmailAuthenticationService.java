package com.anascoding.auth_system.service.email;


import com.anascoding.auth_system.dto.request.EmailAuthRequest;
import com.anascoding.auth_system.dto.response.EmailAuthResponse;
import com.anascoding.auth_system.entity.AppAuthProvider;
import com.anascoding.auth_system.entity.AppUser;
import com.anascoding.auth_system.entity.Role;
import com.anascoding.auth_system.security.jwt.JwtUtils;
import com.anascoding.auth_system.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailAuthenticationService {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepo appUserRepo;
    private final AuthenticationManager authManager;
    private final EmailVerificationService verificationService;

    public EmailAuthResponse authenticate(EmailAuthRequest request) {
        // check if user is already exist or not
        // if exist > validate username and password
        // if not exist > register + send verification email
        Optional<AppUser> userFromDb = appUserRepo
                .findByEmail(request.email());

        if(userFromDb.isEmpty()){
            AppUser newUser = registerNewUser(request);
            this.verificationService.sendVerificationEmail(request.email());
        }
        // need to convert optional user to AppUser
        AppUser user = userFromDb.get();

        // Check if email is verified (Some users add email and password and wait till email verification)
        if(!user.isEmailVerified()){
            this.verificationService.sendVerificationEmail(user.getEmail());
        }

        // try to authenticate User
        // if succeeded > issue a refresh / access tokens else we verify email and generate token
        authenticateUser(request);

        return generateAccessToken(user);
    }

    private EmailAuthResponse generateAccessToken(AppUser userFromDb) {
        final String accessToken = this.jwtUtils.generateAccessToken(userFromDb.getEmail());
        final String refreshToken = this.jwtUtils.generateRefreshToken(userFromDb.getEmail());

        return EmailAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    private Role chooseRole(String role){
        // customer > is the one who offers jobs to workers
        // worker is like electrician, plumber
      return role.matches("Customer") ? Role.CUSTOMER : Role.WORKER;
    }

    private AppUser registerNewUser(EmailAuthRequest request){
        final AppUser newAppUser =
                AppUser
                        .builder()
                        .email(request.email())
                        .password(passwordEncoder.encode( request.password()) )
                        .appAuthProvider(AppAuthProvider.LOCAL)
                        .emailVerified(false)
                        .role(this.chooseRole(request.role()))
                        .build();
        return appUserRepo.save(newAppUser);
    }

    private void authenticateUser(EmailAuthRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ) ;

        // Used Auth Manager to validate username and encoded password
        // if user not valid throw AuthenticationException
        // Only Who Set Authentication > JwtAuthFilter + AuthManager (Not a service)
        authManager.authenticate(authToken);

        // dangerous
//        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
