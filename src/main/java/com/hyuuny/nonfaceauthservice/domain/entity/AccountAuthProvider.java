package com.hyuuny.nonfaceauthservice.domain.entity;

import com.hyuuny.nonfaceauthservice.common.constants.AuthProvider;
import com.hyuuny.nonfaceauthservice.common.jpa.domain.AuditTable;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static org.springframework.util.ObjectUtils.isEmpty;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerUserIdentifier"})
})
@Entity
public class AccountAuthProvider extends AuditTable {

    @ManyToOne(optional = false, fetch = LAZY)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerUserIdentifier;

    public void assignAccount(Account account) {
        if (!isEmpty(this.account)) {
            this.account.getAuthProviders().remove(this);
        }
        this.account = account;
        this.account.getAuthProviders().add(this);
    }

}
