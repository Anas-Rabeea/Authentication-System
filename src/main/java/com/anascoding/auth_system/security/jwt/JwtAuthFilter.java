package com.anascoding.auth_system.security.jwt;

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

        var claim = jwtUtils.extractClaimByKey(extractedToken , "type");

        if(claim == null)
            return false;

        return TokenType.ACCESS_TOKEN.name().equals(claim.toString());

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {

        // TODO -- what if the user is already authenticated and go to this endpoint > redirect to /feed
        String path = request.getRequestURI();
        // no JWT Validation for this endpoint (Extra step plus the configs in the SecurityConfig Matchers
        if (path.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }


        if(request.getHeader("Authorization") !=null) {
            // we will use users email as the token username
            String extractedToken = request.getHeader("Authorization");

            // check if this a Bearer token
            if (extractedToken.startsWith("Bearer ")) {
                extractedToken = extractedToken.substring(7);
            } else {
                // go to the next filter
                filterChain.doFilter(request, response);
                return;
            }

            // check if the token is an access token
            if (!isAccessToken(extractedToken)) {
                filterChain.doFilter(request, response);
                return;
            }

            String tokenUsername = jwtUtils.extractTokenUserName(extractedToken);

            // check if user is not already authenticated
            if ( SecurityContextHolder.getContext().getAuthentication() == null
                    && tokenUsername != null) {
                // if user is not authenticated , then
                // we validate the subject of the token and see if it is a valid token or not

                // Actual Existing User
                UserDetails expectedUser = userDetailsService.loadUserByUsername(tokenUsername);

                // check if users are matched = user in the DB
                // and also if the token is a valid = not expired + sign key is right
                if (jwtUtils.isTokenValid(extractedToken, expectedUser.getUsername())) {
                    // set this username in the token as authenticated in the SecurityContextHolder
                    // via the Authentication Token
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    // SecurityContext principal = email
                                    expectedUser.getUsername(),
                                    null,
                                    expectedUser.getAuthorities());
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // set user in the current SecurityContextHolder = this user is currently authenticated
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
