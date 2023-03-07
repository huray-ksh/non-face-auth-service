package com.hyuuny.nonfaceauthservice.domain.entity;

import com.google.common.collect.Sets;
import com.hyuuny.nonfaceauthservice.common.code.ResponseCode;
import com.hyuuny.nonfaceauthservice.common.exception.HttpStatusMessageException;
import com.hyuuny.nonfaceauthservice.common.jpa.converter.UUIDConverter;
import com.hyuuny.nonfaceauthservice.common.jpa.domain.AuditTable;
import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;
import static org.springframework.util.ObjectUtils.isEmpty;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Account extends AuditTable {

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ACTIVATION("활성화"),
        DEACTIVATION("비활성화"),
        LEAVE("탈퇴");

        public boolean isActiveUser() {
            return this == ACTIVATION;
        }

        private final String title;
    }

    @Default
    @Convert(converter = UUIDConverter.class)
    @Column(unique = true, nullable = false)
    private UUID uid = UUID.randomUUID();

    @Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVATION;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String name;

    private String thumbnailUrl;

    @OneToOne(cascade = {PERSIST, MERGE}, orphanRemoval = true)
    private AccountProfile accountProfile;

    @ManyToOne(optional = false)
    private Role role;

    @Default
    @OneToMany(mappedBy = "account", cascade = {PERSIST, MERGE}, orphanRemoval = true, fetch = EAGER)
    private Set<AccountAuthProvider> authProviders = Sets.newHashSet();

    public void signUp(Role role, List<Privilege> privileges, PasswordEncoder encoder) {
        if (isEmpty(this.accountProfile.getPassword()) && !isEmpty(this.authProviders)) {
            this.accountProfile.assignUuid();
        }

        this.accountProfile.signUp(encoder);
        role.getAccounts().clear();
        role.addAccount(this);
        role.getRolePrivileges().clear();
        privileges.forEach(role::addPrivilege);
    }

    public void assignRole(Role role) {
        if (!isEmpty(this.role)) {
            this.role.getAccounts().remove(this);
        }
        this.role = role;
        this.role.getAccounts().add(this);
    }

    public void addAuthProvider(AccountAuthProvider accountAuthProvider) {
        accountAuthProvider.assignAccount(this);
    }

    public List<Privilege.Type> getPrivilegeTypes() {
        return this.role.getRolePrivileges().stream()
                .map(RolePrivilege::getType)
                .toList();
    }

    public void signIn(String rawPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(rawPassword, this.accountProfile.getPassword())) {
            throw new HttpStatusMessageException(ResponseCode.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

}
