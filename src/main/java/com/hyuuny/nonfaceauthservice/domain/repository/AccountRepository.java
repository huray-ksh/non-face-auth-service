package com.hyuuny.nonfaceauthservice.domain.repository;


import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountProfileUsername(final String username);

}
