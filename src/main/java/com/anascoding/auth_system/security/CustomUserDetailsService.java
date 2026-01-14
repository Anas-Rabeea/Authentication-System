package com.anascoding.auth_system.security;

import com.anascoding.auth_system.repository.AppUserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepo userRepo;

    public CustomUserDetailsService(AppUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepo.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found.")
        );
    }
}
