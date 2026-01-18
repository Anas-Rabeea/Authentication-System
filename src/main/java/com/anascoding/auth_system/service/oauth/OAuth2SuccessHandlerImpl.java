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

    private final AppUserRepo appUserRepo;
    private final JwtUtils jwtUtils;

    // App > Auth Provider > Jwt
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException
    {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        final String authProvider =
                ((OAuth2AuthenticationToken) authentication)
                                .getAuthorizedClientRegistrationId()
                                .toUpperCase();
        final String providerId = this.extractProviderId(authProvider , oAuth2User);

        final String oauthEmail =
                oAuth2User.getAttribute("email");

        AppUser user =
                appUserRepo
                        .findByAppAuthProviderAndProviderId(
                            AppAuthProvider.valueOf(authProvider.toUpperCase()),
                            providerId)
                        .orElseGet(() ->
                                this.registerNewOAuth2User(
                                        AppAuthProvider.valueOf(authProvider.toUpperCase()),
                                        providerId,
                                        oauthEmail));

        final String  jwtSubject = user.getUsername();
        String accessToken = this.jwtUtils.generateAccessToken(jwtSubject);
        String refreshToken = this.jwtUtils.generateAccessToken(jwtSubject);



        response.setContentType("application/json");
        response.getWriter().write(
                String.valueOf(Map.of(
                        "accessToken" , accessToken ,
                        "refreshToken",refreshToken)));
    }

    private AppUser registerNewOAuth2User(
            AppAuthProvider appAuthProvider,
            String providerId,
            String oauthEmail)
    {
        return AppUser
                .builder()
                .email(oauthEmail)
                .emailVerified(true) // oatuh2 users are verified/legit users
                .providerId(providerId)
                .appAuthProvider(appAuthProvider)
                .role(Role.CUSTOMER) // TODO -- refactor this to accept the role from the request
                .build();
    }

    private String extractProviderId(
            String authProvider,
            OAuth2User oAuth2User)
    {
        return switch (authProvider) {
            case "GOOGLE" -> oAuth2User.getAttribute("sub");
            case "FACEBOOK" -> oAuth2User.getAttribute("id");
            default -> throw new IllegalArgumentException("Unknown Authentication Provider. Must be (FACEBOOK , GOOGLE)");
        };
    }

}
