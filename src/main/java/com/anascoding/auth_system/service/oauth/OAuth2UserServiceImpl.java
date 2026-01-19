package com.anascoding.auth_system.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // this class is ONLY Responsible for fetch/create user data from the provider
    // private final AppUserRepo appUserRepo;

    // fetch the raw OAUth2 User Data
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        return new DefaultOAuth2UserService().loadUser(userRequest);
    }
}

//        OAuth2User oAuth2User =
//                new DefaultOAuth2UserService().loadUser(userRequest);
//
//        String provider = // FACEBOOK or GOOGLE
//                userRequest.getClientRegistration()
//                        .getRegistrationId()
//                        .toUpperCase();
//
//        String providerId = extractProviderId(provider, oAuth2User); // ID of the user
//        String email = oAuth2User.getAttribute("email"); // user email
//
//        AppUser user =
//                appUserRepo
//                        .findByAppAuthProviderAndProviderId(
//                                AppAuthProvider.valueOf(provider),
//                                providerId)
//                        .orElseGet(() ->
//                                registerNewOAuth2User(
//                                        AppAuthProvider.valueOf(provider),
//                                        providerId,
//                                        email));
//
//        // add The logged OAuth2User to the attributes of oauth2user as attribute to access AppUser data
//        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
//        attributes.put("AppUser", user);
//
//        return new DefaultOAuth2User(
//                oAuth2User.getAuthorities(),
//                attributes,
//                "email"
//        );
//    }
//
//    private AppUser registerNewOAuth2User(
//            AppAuthProvider provider,
//            String providerId,
//            String email) {
//
//        AppUser user = AppUser.builder()
//                .email(email)
//                .emailVerified(true)
//                .providerId(providerId)
//                .appAuthProvider(provider)
//                .role(Role.CUSTOMER)
//                .build();
//
//        return appUserRepo.save(user);
//    }
//
//    private String extractProviderId(
//            String provider,
//            OAuth2User oAuth2User) {
//
//        return switch (provider) {
//            case "GOOGLE" -> oAuth2User.getAttribute("sub"); // Google uses sub
//            case "FACEBOOK" -> oAuth2User.getAttribute("id"); // Facebook uses id as providerID (user client ID)
//            default -> throw new IllegalArgumentException(
//                    "Unsupported OAuth provider: " + provider);
//        };
//    }
//}
