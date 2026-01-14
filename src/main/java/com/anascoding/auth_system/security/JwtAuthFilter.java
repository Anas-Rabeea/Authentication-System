package com.anascoding.auth_system.security;

import com.anascoding.auth_system.jwt.JwtUtils;
import com.anascoding.auth_system.jwt.TokenType;
import com.anascoding.auth_system.repository.AppUserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtUtils jwtUtils;
        private final UserDetailsService userDetailsService;

//        private boolean isUserNamesMatched(String expectedUserName , String tokenUserName){
//            return expectedUserName.matches(tokenUserName);
//        }

    private boolean isAccessToken(String extractedToken){
        return this
                .jwtUtils
                .extractClaimByKey(extractedToken ,"type")
                .equals(TokenType.ACCESS_TOKEN);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {

        // we will use users email as the token username

        String extractedToken = request.getHeader("Authorization");

        // check if this a Bearer token
        if (extractedToken.startsWith("Bearer ")){
        extractedToken = extractedToken.substring(7);
        }
        else {
            // go to the next filter
            filterChain.doFilter(request,response);
            return;
        }

        // check if the token is an access token
        if(!isAccessToken(extractedToken) ){
            filterChain.doFilter(request,response);
            return;
        }

        String tokenUsername = jwtUtils.extractTokenUserName(extractedToken);

        // check if user is not already authenticated
        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
           && tokenUsername != null ){
            // if user is not authenticated , then
            // we validate the subject of the token and see if it is a valid token or not

            // Actual Existing User
            UserDetails expectedUser = userDetailsService.loadUserByUsername(tokenUsername);

            // check if users are matched = user in the DB
            // and also if the token is a valid = not expired + sign key is right
            if (jwtUtils.isTokenValid(extractedToken , expectedUser.getUsername())){
                // set this username in the token as authenticated in the SecurityContextHolder
                // via the Authentication Token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                // SecurityContext principal = email
                                expectedUser.getUsername() ,
                                null ,
                                expectedUser.getAuthorities()) ;
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // set user in the current SecurityContextHolder = this user is currently authenticated
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
