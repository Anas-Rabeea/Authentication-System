package com.anascoding.auth_system.service.oauth;

import com.anascoding.auth_system.entity.AppAuthProvider;
import com.anascoding.auth_system.entity.AppUser;
import com.anascoding.auth_system.entity.Role;
import com.anascoding.auth_system.repository.AppUserRepo;
import com.anascoding.auth_system.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
     private final AppUserRepo appUserRepo;

    // App > Auth Provider > Jwt
    // App > Google/Facebook > OAuth2ServiceImpl (Get Raw OAuth2User Data) >
    // OAuth2SuccessHandlerImpl (find/create user + return JWT in the response ) > Jwt
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException
    {
        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;

        OAuth2User oAuth2User = token.getPrincipal();

        String provider = // FACEBOOK or GOOGLE
                token
                        .getAuthorizedClientRegistrationId()
                        .toUpperCase();

        String providerId = extractProviderId(provider, oAuth2User); // ID of the user
        String email = oAuth2User.getAttribute("email"); // user email

        AppUser user =
                appUserRepo
                        .findByAppAuthProviderAndProviderId(
                                AppAuthProvider.valueOf(provider),
                                providerId)
                        .orElseGet(() ->
                                registerNewOAuth2User(
                                        AppAuthProvider.valueOf(provider),
                                        providerId,
                                        email));


        String  jwtSubject = user.getUsername();
        String accessToken = this.jwtUtils.generateAccessToken(jwtSubject);
        String refreshToken = this.jwtUtils.generateRefreshToken(jwtSubject);

        response.getWriter().write(
                String.valueOf(Map.of(
                        "accessToken" , accessToken ,
                        "refreshToken",refreshToken)));
    }

    private AppUser registerNewOAuth2User(
            AppAuthProvider provider,
            String providerId,
            String email) {

        AppUser user = AppUser.builder()
                .email(email)
                .emailVerified(true)
                .providerId(providerId)
                .appAuthProvider(provider)
                .role(Role.CUSTOMER)
                .build();

        return appUserRepo.save(user);
    }

    private String extractProviderId(
            String provider,
            OAuth2User oAuth2User) {

        return switch (provider) {
            case "GOOGLE" -> oAuth2User.getAttribute("sub"); // Google uses sub
            case "FACEBOOK" -> oAuth2User.getAttribute("id"); // Facebook uses id as providerID (user client ID)
            default -> throw new IllegalArgumentException(
                    "Unsupported OAuth provider: " + provider);
        };
    }

}
