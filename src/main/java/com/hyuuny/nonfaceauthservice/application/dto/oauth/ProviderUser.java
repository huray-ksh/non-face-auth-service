package com.hyuuny.nonfaceauthservice.application.dto.oauth;


import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;

public interface ProviderUser {

    String getId();

    String getName();

    String getUsername();

    String getPassword();

    String getMobilePhoneNumber();

    Gender getGender();

    String getProviderId();

    String getProfileImageUrl();

    Map<String, Object> getAttributes();

    List<? extends GrantedAuthority> getAuthorities();

}
