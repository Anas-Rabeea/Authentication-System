package com.anascoding.auth_system.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        try {
            Object claim = jwtUtils.extractClaimByKey(extractedToken, "type");
            return TokenType.ACCESS_TOKEN.name().equals(String.valueOf(claim));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {

        // TODO -- what if the user is already authenticated and go to this endpoint > redirect to /feed
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String extractedToken = authorizationHeader.substring(7);

        // check if the token is an access token
        if (!isAccessToken(extractedToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        String tokenUserName ;
        try{
            tokenUserName = this.jwtUtils.extractTokenUserName(extractedToken);
        } catch (Exception e) {
            filterChain.doFilter(request,response);
            return;
        }
        // check if user is not already authenticated
        if(SecurityContextHolder.getContext().getAuthentication() == null &&
            tokenUserName != null)
        {
            UserDetails user =
                    this.userDetailsService.loadUserByUsername(tokenUserName);
            if(this.jwtUtils.isTokenValid(extractedToken,user.getUsername()))
            {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
