package com.hyuuny.nonfaceauthservice.domain.entity;

import com.hyuuny.nonfaceauthservice.common.jpa.domain.AuditTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;


@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(indexes = @Index(name = "idx_userId_001", columnList = "userId", unique = true))
public class ExchangeCode extends AuditTable {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String code;

}
