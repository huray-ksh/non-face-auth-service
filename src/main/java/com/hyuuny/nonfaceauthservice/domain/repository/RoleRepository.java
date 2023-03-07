package com.hyuuny.nonfaceauthservice.domain.repository;


import com.hyuuny.nonfaceauthservice.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByType(final Role.Type type);

}
