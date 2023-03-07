package com.hyuuny.nonfaceauthservice.domain.repository;

import com.hyuuny.nonfaceauthservice.domain.entity.ExchangeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeCodeRepository extends JpaRepository<ExchangeCode, Long> {

    Optional<ExchangeCode> findByCode(final String code);

    Optional<ExchangeCode> findByCodeAndUserId(final String code, final Long userId);

}
