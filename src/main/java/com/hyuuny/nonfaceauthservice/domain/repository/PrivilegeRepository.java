package com.hyuuny.nonfaceauthservice.domain.repository;


import com.hyuuny.nonfaceauthservice.domain.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    List<Privilege> findAllByTypeIn(List<Privilege.Type> privilegeTypes);

}
