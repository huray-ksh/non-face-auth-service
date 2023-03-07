package com.hyuuny.nonfaceauthservice.application;

import com.hyuuny.nonfaceauthservice.application.dto.AccountDto.Response;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.service.AccountDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountDomainService accountDomainService;

    public Response getAccount(final Long id) {
        Account existingAccount = accountDomainService.loadUser(id);
        return toResponse(existingAccount);
    }

    private Response toResponse(Account account) {
        return new Response(account);
    }

    public Response getAccount(final String username) {
        Account existingAccount = accountDomainService.loadUser(username);
        return toResponse(existingAccount);
    }

}
