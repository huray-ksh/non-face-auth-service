package com.hyuuny.nonfaceauthservice.domain.service;

import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.application.dto.AccountDto;
import com.hyuuny.nonfaceauthservice.common.code.ResponseCode;
import com.hyuuny.nonfaceauthservice.common.constants.AuthProvider;
import com.hyuuny.nonfaceauthservice.common.exception.HttpStatusMessageException;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.entity.AccountAuthProvider;
import com.hyuuny.nonfaceauthservice.domain.entity.Privilege;
import com.hyuuny.nonfaceauthservice.domain.entity.Role;
import com.hyuuny.nonfaceauthservice.domain.repository.AccountDomainRepository;
import com.hyuuny.nonfaceauthservice.domain.repository.AccountRepository;
import com.hyuuny.nonfaceauthservice.domain.repository.PrivilegeRepository;
import com.hyuuny.nonfaceauthservice.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class AccountDomainService {

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PrivilegeRepository privilegeRepository;

    private final AccountDomainRepository accountDomainRepository;

    private final PasswordEncoder passwordEncoder;

    public Account signUp(AccountDto.SignUpRequest request) {
        Role role = findRole(request.getRoleType());
        List<Privilege> privileges = privilegeRepository.findAllByTypeIn(request.getPrivilegeTypes());
        Account newAccount = request.toEntity();
        newAccount.signUp(role, privileges, passwordEncoder);
        return accountRepository.save(newAccount);
    }

    public Role findRole(Role.Type type) {
        return roleRepository.findByType(type).orElseThrow(
                () -> new HttpStatusMessageException(ResponseCode.BAD_REQUEST, "role을 찾을 수 없습니다.")
        );
    }

    public Account loadUser(final Long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new HttpStatusMessageException(ResponseCode.BAD_REQUEST, id + "번 회원을 찾을 수 없습니다.")
        );
    }

    public AccountAdapter getUserDetails(final Long id) {
        Account existingAccount = loadUser(id);
        List<Privilege.Type> privilegeTypes = existingAccount.getPrivilegeTypes();
        return new AccountAdapter(existingAccount, privilegeTypes);
    }

    public Account loadUser(final String username) {
        return accountRepository.findByAccountProfileUsername(username).orElseThrow(
                () -> new HttpStatusMessageException(ResponseCode.NO_MATCHED_USERNAME, "일치하는 username이 없습니다.")
        );
    }

    public Optional<Account> loadOptionalAccount(final String username) {
        return accountRepository.findByAccountProfileUsername(username);
    }

    public Account loadExchangeCodeCodeAndUserId(final String code, final Long userId) {
        return accountDomainRepository.findAccountByExchangeCodeAndUserId(code, userId).orElseThrow(
                () -> new HttpStatusMessageException(ResponseCode.BAD_REQUEST, userId + "번 회원을 찾을 수 없습니다.")
        );
    }

    public Long loadAccountIdByAuthProvider(AuthProvider provider, String providerUserIdentifier) {
        Optional<AccountAuthProvider> optional = accountDomainRepository.findAccountAuthProvider(provider, providerUserIdentifier);
        return optional.map(accountAuthProvider -> accountAuthProvider.getAccount().getId()).orElse(null);
    }

}
