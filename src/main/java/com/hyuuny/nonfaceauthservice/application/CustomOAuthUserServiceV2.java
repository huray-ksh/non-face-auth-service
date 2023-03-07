package com.hyuuny.nonfaceauthservice.application;

import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.OAuth2Adapter;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.ProviderUser;
import com.hyuuny.nonfaceauthservice.common.abstraction.AbstractOAuth2UserService;
import com.hyuuny.nonfaceauthservice.common.constants.AuthProvider;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.service.AccountDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuthUserServiceV2 extends AbstractOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AccountDomainService accountDomainService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        ProviderUser providerUser = super.providerUser(clientRegistration, oAuth2User);
        AuthProvider registrationId = AuthProvider.valueOf(providerUser.getProviderId().toUpperCase());
        log.info("oauthUser=[{}][{}][{}]", registrationId.name(), providerUser.getUsername(), providerUser.getName());

        // 기존 소셜 가입 내역이 있으면 이미 가입된 내역이 있다는 예외 반환
        // 카카오는 인증 안 하면 식별 값 X
        Long accountId = accountDomainService.loadAccountIdByAuthProvider(registrationId, providerUser.getId());
        return isEmpty(accountId)
                ? new OAuth2Adapter(providerUser)
                : accountDomainService.getUserDetails(accountId);
    }

}
