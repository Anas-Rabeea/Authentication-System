package com.anascoding.auth_system.config;

import com.anascoding.auth_system.entity.AppAuthProvider;
import com.anascoding.auth_system.entity.AppUser;
import com.anascoding.auth_system.entity.Role;
import com.anascoding.auth_system.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StartUp implements CommandLineRunner {

    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if(appUserRepo.count() == 0){

            AppUser appUserDeveloper =
                    AppUser
                        .builder()
                        .email("developer-backend@mehna.com")
                        .password(passwordEncoder.encode("P#@!sswor$#!!99"))
                        .role(Role.DEVELOPER)
                        .appAuthProvider(AppAuthProvider.EMAIL)
                        .emailVerified(true)
                        .build();
            AppUser appUserAdmin =
                    AppUser
                            .builder()
                            .email("admin-backend@mehna.com")
                            .password(passwordEncoder.encode("P#@!sswor$#!!99"))
                            .role(Role.ADMIN)
                            .appAuthProvider(AppAuthProvider.EMAIL)
                            .emailVerified(true)
                            .build();

            this.appUserRepo.saveAll(List.of(appUserAdmin, appUserDeveloper));
        }




    }
}
