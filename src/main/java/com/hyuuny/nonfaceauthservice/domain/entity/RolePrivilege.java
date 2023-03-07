package com.hyuuny.nonfaceauthservice.domain.entity;

import com.hyuuny.nonfaceauthservice.common.jpa.domain.AuditTable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.EqualsAndHashCode.Include;


@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class RolePrivilege extends AuditTable {

    @Include
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Role role;

    @Include
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Privilege privilege;


    public void assignRole(Role role) {
        if (this.role != null) {
            this.role.getRolePrivileges().remove(this);
        }
        this.role = role;
        this.role.getRolePrivileges().add(this);
    }

    public Privilege.Type getType() {
        return this.privilege.getType();
    }

}
