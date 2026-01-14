package com.anascoding.auth_system.entity;

// Authentication Mechanism AuthManager >  authprovider > UserDetailsService > UserDetails
public enum AppAuthProvider {
    LOCAL, // email or Phone
    GOOGLE, // OAuth2
    FACEBOOK; // OAuth2
}
