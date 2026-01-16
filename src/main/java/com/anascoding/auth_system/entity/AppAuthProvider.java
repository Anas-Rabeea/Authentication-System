package com.anascoding.auth_system.entity;

// Authentication Mechanism AuthManager >  authprovider > UserDetailsService > UserDetails
public enum AppAuthProvider {
    LOCAL, // email
    PHONE,
    GOOGLE, // OAuth2
    FACEBOOK; // OAuth2
}
