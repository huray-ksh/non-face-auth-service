package com.hyuuny.nonfaceauthservice.application.dto.oauth;

import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Objects;


public class KakaoUser extends OAuth2ProviderUser {

    private String id;

    private Map<String, Object> attributeProfile;

    public KakaoUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super((Map<String, Object>) oAuth2User.getAttributes().get("kakao_account"), oAuth2User, clientRegistration);
        this.id = String.valueOf(oAuth2User.getAttributes().get("id"));
        this.attributeProfile = (Map<String, Object>) super.getAttributes().get("profile");
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return (String) this.attributeProfile.get("nickname");
    }

    @Override
    public String getMobilePhoneNumber() {
        return (String) this.attributeProfile.get("phone_number");
    }

    @Override
    public Gender getGender() {
        return Objects.equals(this.getAttributes().get("gender"), "male") ? Gender.MALE : Gender.FEMALE;
    }

    @Override
    public String getProfileImageUrl() {
        return (String) this.attributeProfile.get("profile_image");
    }

}
