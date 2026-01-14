package com.anascoding.auth_system.repository;

import com.anascoding.auth_system.entity.AppAuthProvider;
import com.anascoding.auth_system.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser,String> {


    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByPhone(String phone);

    boolean existsByEmailVerified(boolean emailVerified);
    boolean existsByPhoneVerified(boolean phoneVerified);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Optional<AppUser> findByAppAuthProviderAndProviderId(
            AppAuthProvider appAuthProvider, String providerId);
}
