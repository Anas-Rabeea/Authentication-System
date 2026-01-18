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

        Optional<AppUser> userFromDb = appUserRepo
                .findByEmail(request.email());

        if(userFromDb.isEmpty()){
            AppUser newUser = registerNewUser(request);
            this.verificationService.sendVerificationEmail(request.email());
        }
        AppUser user = userFromDb.get();

        if(!user.isEmailVerified()){
            this.verificationService.sendVerificationEmail(user.getEmail());
        }

        authenticateUser(user);

        return generateAccessToken(user);
    }

    private EmailAuthResponse generateAccessToken(AppUser userFromDb) {
        final String accessToken = this.jwtUtils.generateAccessToken(userFromDb.getUsername());
        final String refreshToken = this.jwtUtils.generateRefreshToken(userFromDb.getUsername());

        return EmailAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Role chooseRole(String role){

      return role.matches("Customer") ? Role.CUSTOMER : Role.WORKER;
    }

    private AppUser registerNewUser(EmailAuthRequest request){
        final AppUser newAppUser =
                AppUser
                        .builder()
                        .email(request.email())
                        .password(passwordEncoder.encode( request.password()) )
                        .appAuthProvider(AppAuthProvider.EMAIL)
                        .emailVerified(false)
                        .role(this.chooseRole(request.role()))
                        .build();
        return appUserRepo.save(newAppUser);
    }

    private void authenticateUser(AppUser user) {


        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                ) ;
        authManager.authenticate(authToken);
    }
}
