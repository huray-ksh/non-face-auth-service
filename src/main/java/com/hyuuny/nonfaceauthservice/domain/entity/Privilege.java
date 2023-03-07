package com.hyuuny.nonfaceauthservice.domain.entity;

import com.google.common.collect.Lists;
import com.hyuuny.nonfaceauthservice.common.jpa.domain.AuditTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.util.List;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Privilege extends AuditTable {

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        PRIVILEGE_ORGANIZATION_C("조직을 등록할 수 있는 권한"),
        PRIVILEGE_ORGANIZATION_R("조직을 조회할 수 있는 권한"),
        PRIVILEGE_ORGANIZATION_U("조직을 수정할 수 있는 권한"),
        PRIVILEGE_ORGANIZATION_D("조직을 삭제할 수 있는 권한"),
        PRIVILEGE_USER_C("회원이 등록할 수 있는 권한"),
        PRIVILEGE_USER_R("회원이 조회할 수 있는 권한"),
        PRIVILEGE_USER_U("회원이 수정할 수 있는 권한"),
        PRIVILEGE_USER_D("회원이 삭제할 수 있는 권한");

        private final String title;
    }

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String description;

    @Default
    @OneToMany(mappedBy = "privilege", cascade = {PERSIST, MERGE}, orphanRemoval = true, fetch = LAZY)
    private List<RolePrivilege> rolePrivileges = Lists.newArrayList();

}
