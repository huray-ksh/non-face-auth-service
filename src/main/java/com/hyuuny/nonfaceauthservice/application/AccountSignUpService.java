package com.hyuuny.nonfaceauthservice.application;

import com.hyuuny.nonfaceauthservice.application.dto.AccountDto;
import com.hyuuny.nonfaceauthservice.application.dto.AccountDto.SignUpRequest;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.ProviderUser;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.service.AccountDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountSignUpService {

    private final AccountDomainService accountDomainService;

    @Transactional
    public AccountDto.Response signUp(SignUpRequest dto) {
        Account signUpAccount = accountDomainService.signUp(dto);
        return toResponse(signUpAccount);
    }

    private AccountDto.Response toResponse(Account signUpAccount) {
        return new AccountDto.Response(signUpAccount);
    }

    @Deprecated
    @Transactional
    public Account signUp(ProviderUser providerUser, String registrationId) {
        Optional<Account> optionalAccount = accountDomainService.loadOptionalAccount(providerUser.getUsername());
        if (optionalAccount.isEmpty()) {
            SignUpRequest dto = SignUpRequest.toAuthSignUpRequest(providerUser, registrationId);
            return accountDomainService.signUp(dto);
        }
        Account account = optionalAccount.get();
        account.getPrivilegeTypes();
        return account;
    }

}
