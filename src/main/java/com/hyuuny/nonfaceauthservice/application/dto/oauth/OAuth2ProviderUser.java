package com.hyuuny.nonfaceauthservice.application.dto.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class OAuth2ProviderUser implements ProviderUser {

    private Map<String, Object> attributes;

    private OAuth2User oAuth2User;

    private ClientRegistration clientRegistration;

    public OAuth2ProviderUser(Map<String, Object> authorities, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        this.attributes = authorities;
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
    }

    @Override
    public String getPassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getUsername() {
        return (String) this.attributes.get("email");
    }

    @Override
    public String getProviderId() {
        return this.clientRegistration.getRegistrationId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return this.oAuth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList();
    }
}
