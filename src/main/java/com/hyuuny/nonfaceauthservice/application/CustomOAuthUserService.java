package com.hyuuny.nonfaceauthservice.application;

import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.ProviderUser;
import com.hyuuny.nonfaceauthservice.common.abstraction.AbstractOAuth2UserService;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Deprecated
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuthUserService extends AbstractOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AccountSignUpService accountSignUpService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        ProviderUser providerUser = super.providerUser(clientRegistration, oAuth2User);
        String registrationId = providerUser.getProviderId();
        log.info("oauthUser=[{}][{}][{}]", registrationId, providerUser.getUsername(), providerUser.getName());

        Account newAccount = accountSignUpService.signUp(providerUser, registrationId);
        return new AccountAdapter(newAccount, newAccount.getPrivilegeTypes(), providerUser);
    }

}
