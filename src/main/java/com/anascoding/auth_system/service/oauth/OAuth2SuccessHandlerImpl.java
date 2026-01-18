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
    // App > Google/Facebook > OAuth2ServiceImpl > OAuth2SuccessHandlerImpl > Jwt
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException
    {
        final OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        assert oAuth2User != null;
        final AppUser user = (AppUser) oAuth2User.getAttribute("AppUser");


        assert user != null;
        final String  jwtSubject = user.getUsername();
        String accessToken = this.jwtUtils.generateAccessToken(jwtSubject);
        String refreshToken = this.jwtUtils.generateAccessToken(jwtSubject);



        response.setContentType("application/json");
        response.getWriter().write(
                String.valueOf(Map.of(
                        "accessToken" , accessToken ,
                        "refreshToken",refreshToken)));
    }

}
