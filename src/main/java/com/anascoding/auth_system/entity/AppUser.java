package com.anascoding.auth_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static com.anascoding.auth_system.entity.AppAuthProvider.LOCAL;

@Entity
@Table(name = "app-user")
@Builder
@NoArgsConstructor @AllArgsConstructor  @Getter @Setter
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id ;

    @Column(name = "email" , nullable = true , unique = true)
    private String email; // this will bt the username which will be loaded from loadByUserName()

    @Column(name = "phone" , nullable = true , unique = true)
    private String phone;

    private String password;

    // for verification
    private boolean emailVerified;
    private boolean phoneVerified;

    // for oAuth2
    @Enumerated(EnumType.STRING)
    private AppAuthProvider appAuthProvider; // this will be the type of authentication
    private String providerId; // OAuth ID Like Google or Facebook ID

    // Authorization
    @Enumerated(EnumType.STRING)
    private Role role;



    @Override
    public boolean isEnabled() {
        if(!appAuthProvider.equals(LOCAL))
            return true; // doesn't matter for oatuh2
        return emailVerified ;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name() ));
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
