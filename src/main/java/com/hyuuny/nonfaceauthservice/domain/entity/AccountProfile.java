package com.hyuuny.nonfaceauthservice.domain.entity;

import com.hyuuny.nonfaceauthservice.common.jpa.domain.AuditTable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;
import static org.springframework.util.ObjectUtils.isEmpty;


@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = @Index(name = "idx_username_001", columnList = "username", unique = true))
@Entity
public class AccountProfile extends AuditTable {

    @OneToOne(mappedBy = "accountProfile", fetch = LAZY)
    private Account account;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String mobilePhoneNumber;

    @LastModifiedDate
    private LocalDateTime lastLoginAt;

    public void assignUuid() {
        this.password = UUID.randomUUID().toString();
    }

    public void signUp(PasswordEncoder encoder) {
        if (!isEmpty(this.password)) {
            this.password = encoder.encode(this.password);
        }
    }

}
