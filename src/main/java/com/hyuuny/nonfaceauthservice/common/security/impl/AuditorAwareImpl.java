package com.hyuuny.nonfaceauthservice.common.security.impl;

import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.common.abstraction.AbstractAuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl extends AbstractAuditorAware {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(authentication -> !AnonymousAuthenticationToken.class
                        .isAssignableFrom(authentication.getClass()))
                .map(Authentication::getPrincipal)
                .filter(principal -> AccountAdapter.class
                        .isAssignableFrom(principal.getClass()))
                .map(AccountAdapter.class::cast)
                .map(AccountAdapter::getName);
    }

}
