package com.anascoding.auth_system.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.anascoding.auth_system.security.jwt.TokenType.ACCESS_TOKEN;
import static com.anascoding.auth_system.security.jwt.TokenType.REFRESH_TOKEN;

@Component
public class JwtUtils {
        private final JwtProperties jwtProperties;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private Key getSecretKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSECRET_KEY().getBytes());
    }


    public String buildToken(final String username , TokenType tokenType , long expirationDuration)
    {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim("type" , tokenType)
                .setExpiration(new Date(System.currentTimeMillis() + expirationDuration))
                .signWith(this.getSecretKey() , SignatureAlgorithm.HS256)
                .compact();

    }

    // generate methods will be used by our Authentication API
    public String generateAccessToken(String username)
    {
        return this.buildToken(
                username,
                ACCESS_TOKEN ,
                jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String username)
    {
        return this.buildToken(
                username,
                REFRESH_TOKEN ,
                jwtProperties.getRefreshTokenExpiration());
    }


    private Claims extractAllClaims(String token){

        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(this.getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (JwtException exception){
            throw new JwtException("Invalid JWT Token.");
        }
    }

    public Object extractClaimByKey(String token, String claimKey) {
        Claims claims = extractAllClaims(token);

        if (!claims.containsKey(claimKey)) {
            throw new IllegalArgumentException("Claim key not found: " + claimKey);
        }
        return claims.get(claimKey);
    }

    public boolean isTokenExpired(String token){

        Date tokenExpirationDate = this.extractAllClaims(token).getExpiration() ;
        return tokenExpirationDate.before(new Date()); // return true if expired
    }


    public boolean isTokenValid(String token , String expected_username){
        //  expected_username > Username from the DB
        final String tokenUsername = this.extractTokenUserName(token);
        return !this.isTokenExpired(token) && tokenUsername.matches(expected_username);
    }

    public String extractTokenUserName(String accessToken)
    {
        return this.extractAllClaims(accessToken).getSubject();
    }






}
