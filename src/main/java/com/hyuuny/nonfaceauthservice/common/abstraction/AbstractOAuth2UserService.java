package com.hyuuny.nonfaceauthservice.common.abstraction;

import com.hyuuny.nonfaceauthservice.application.dto.oauth.KakaoUser;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.NaverUser;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.ProviderUser;
import com.hyuuny.nonfaceauthservice.common.constants.AuthProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;


@RequiredArgsConstructor
@Getter
public abstract class AbstractOAuth2UserService {

    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();
        AuthProvider authProvider = AuthProvider.valueOf(registrationId.toUpperCase());
        switch (authProvider) {
            case NAVER -> {
                return new NaverUser(oAuth2User, clientRegistration);
            }
            case KAKAO -> {
                return new KakaoUser(oAuth2User, clientRegistration);
            }
        }
        return null;
    }

}
