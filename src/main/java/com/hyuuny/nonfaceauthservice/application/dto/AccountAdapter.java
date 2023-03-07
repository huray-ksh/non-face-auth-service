package com.hyuuny.nonfaceauthservice.application.dto;

import com.google.common.collect.Lists;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.ProviderUser;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.entity.AccountAuthProvider;
import com.hyuuny.nonfaceauthservice.domain.entity.Privilege;
import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

public class AccountAdapter implements UserDetails, OAuth2User {

    private final Account account;

    private final List<Privilege.Type> privilegeTypes;

    private ProviderUser providerUser;

    public AccountAdapter(Account account, List<Privilege.Type> privilegeTypes) {
        this(account, privilegeTypes, null);
    }

    public AccountAdapter(Account account, List<Privilege.Type> privilegeTypes, ProviderUser providerUser) {
        this.account = account;
        this.privilegeTypes = privilegeTypes;
        this.providerUser = providerUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.providerUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = Lists.newArrayList();
        authorities.add(new SimpleGrantedAuthority(this.account.getRole().toType()));
        this.privilegeTypes
                .forEach(privilegeType -> authorities.add(new SimpleGrantedAuthority(privilegeType.name())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.account.getAccountProfile().getPassword();
    }

    @Override
    public String getUsername() {
        return this.account.getAccountProfile().getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.account.getStatus().isActiveUser();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.account.getStatus().isActiveUser();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.account.getStatus().isActiveUser();
    }

    @Override
    public boolean isEnabled() {
        return this.account.getStatus().isActiveUser();
    }

    public Long getUserId() {
        return this.account.getId();
    }

    public String getName() {
        return this.account.getName();
    }

    public Account.Status getStatus() {
        return this.account.getStatus();
    }

    public Gender getGender() {
        return this.account.getGender();
    }

    public String getRoleType() {
        return this.account.getRole().toType();
    }

    public List<AccountAuthProvider> getAccountAuthProviders() {
        return Lists.newArrayList(this.account.getAuthProviders()).stream()
                .sorted(Comparator.comparing(AccountAuthProvider::getCreatedAt).reversed())
                .toList();
    }

    public LocalDateTime getLastLoginAt() {
        return this.account.getAccountProfile().getLastLoginAt();
    }

    public LocalDateTime getCreatedAt() {
        return this.account.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return this.account.getUpdatedAt();
    }

}

