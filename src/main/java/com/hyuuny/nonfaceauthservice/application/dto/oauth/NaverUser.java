package com.hyuuny.nonfaceauthservice.application.dto.oauth;

import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Objects;



public class NaverUser extends OAuth2ProviderUser {

    public NaverUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super((Map<String, Object>) oAuth2User.getAttributes().get("response"), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() {
        return (String) getAttributes().get("id");
    }

    @Override
    public String getName() {
        return (String) getAttributes().get("nickname");
    }

    @Override
    public String getMobilePhoneNumber() {
        return (String) getAttributes().get("mobile");
    }

    @Override
    public Gender getGender() {
        return Objects.equals(getAttributes().get("gender"), "M") ? Gender.MALE : Gender.FEMALE;
    }

    @Override
    public String getProfileImageUrl() {
        return (String) getAttributes().get("profile_image");
    }

}
