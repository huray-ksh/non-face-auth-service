package com.hyuuny.nonfaceauthservice.application.dto.oauth;

import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public record OAuth2Adapter(ProviderUser providerUser) implements UserDetails, OAuth2User {

    @Override
    public String getName() {
        return this.providerUser.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.providerUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.providerUser.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.providerUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.providerUser.getUsername();
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
    public boolean isEnabled() {
        return true;
    }

    public String getId() {
        return this.providerUser.getId();
    }

    public String getMobilePhoneNumber() {
        return this.providerUser.getMobilePhoneNumber();
    }

    public Gender getGender() {
        return this.providerUser.getGender();
    }

    public String getProviderId() {
        return this.providerUser.getProviderId();
    }

    public String getProfileImageUrl() {
        return this.providerUser.getProfileImageUrl();
    }

}
