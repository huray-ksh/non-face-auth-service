package com.hyuuny.nonfaceauthservice.application.dto;

import com.hyuuny.nonfaceauthservice.common.security.provider.JwtProvider;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.entity.ExchangeCode;
import com.hyuuny.nonfaceauthservice.domain.entity.Privilege;
import com.hyuuny.nonfaceauthservice.domain.entity.Role;
import com.hyuuny.nonfaceauthservice.domain.service.AccountDomainService;
import com.hyuuny.nonfaceauthservice.domain.service.ExchangeCodeDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

    private final AccountDomainService accountDomainService;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final ExchangeCodeDomainService exchangeCodeDomainService;


    public AccountAdapter authentication(AuthenticationDto.Credential dto) {
        Account existingAccount = accountDomainService.loadUser(dto.getUsername());
        Role userRole = accountDomainService.findRole(existingAccount.getRole().getType());
        List<Privilege.Type> privilegeTypes = existingAccount.getPrivilegeTypes();
        existingAccount.signIn(dto.getPassword(), passwordEncoder);
        return new AccountAdapter(existingAccount, privilegeTypes);
    }

    public AccountAdapter authentication(final Long id) {
        Account existingAccount = accountDomainService.loadUser(id);
        Role userRole = accountDomainService.findRole(existingAccount.getRole().getType());
        List<Privilege.Type> privilegeTypes = existingAccount.getPrivilegeTypes();
        return new AccountAdapter(existingAccount, privilegeTypes);
    }

    @Transactional
    public AuthenticationDto.AccessToken generateToken(final Long userId) {
        return jwtProvider.issueLoginToken(userId).getAccessToken();
    }

    public AuthenticationDto.AccessToken refreshToken(String accessToken, String refreshToken) {
        jwtProvider.verifyRefreshToken(refreshToken);
        AuthenticationDto.UserWithToken userWithToken = jwtProvider.reissueLoginToken(refreshToken);
//        this.disableToken(accessToken, refreshToken);
        return userWithToken.getAccessToken();
    }
//    public void disableToken(String accessToken, String refreshToken) {
//        jwtProvider.disableToken(accessToken);
//        jwtProvider.disableToken(refreshToken);
//        jwtProvider.deleteRefreshToken(refreshToken);

//    }


}
