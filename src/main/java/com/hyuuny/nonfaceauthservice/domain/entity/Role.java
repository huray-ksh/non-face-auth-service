package com.hyuuny.nonfaceauthservice.domain.entity;

import com.google.common.collect.Sets;
import com.hyuuny.nonfaceauthservice.common.jpa.domain.AuditTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Role extends AuditTable {

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        ROLE_SYS_ADMIN("관리자"),
        ROLE_SYS_PRODUCT_MANAGER("상품 관리 매니저"),
        ROLE_SYS_ORDER_MANAGER("주문 관리 매니저"),
        ROLE_SYS_USER("회원");

        private final String title;
    }

    @Column(nullable = false)
    private String name;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.ROLE_SYS_USER;

    @Column(nullable = false)
    private String description;

    @Default
    @OneToMany(mappedBy = "role", cascade = {PERSIST, MERGE}, orphanRemoval = true, fetch = EAGER)
    private Set<RolePrivilege> rolePrivileges = Sets.newHashSet();

    @Default
    @OneToMany(mappedBy = "role", fetch = LAZY)
    private Set<Account> accounts = Sets.newHashSet();

    public void addAccount(Account account) {
        account.assignRole(this);
    }

    public void addPrivilege(Privilege privilege) {
        RolePrivilege rolePrivilege = RolePrivilege.builder()
                .role(this)
                .privilege(privilege)
                .build();
        rolePrivilege.assignRole(this);
    }

    public String toType() {
        return this.type.name();
    }

}
