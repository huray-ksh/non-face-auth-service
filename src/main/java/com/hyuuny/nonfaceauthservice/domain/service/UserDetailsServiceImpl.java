package com.hyuuny.nonfaceauthservice.domain.service;

import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.entity.Privilege;
import com.hyuuny.nonfaceauthservice.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account existingUser = accountRepository.findByAccountProfileUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username + "을 찾을 수 없습니다.")
        );
        List<Privilege.Type> privilegeTypes = existingUser.getPrivilegeTypes();
        return new AccountAdapter(existingUser, privilegeTypes);
    }

}
