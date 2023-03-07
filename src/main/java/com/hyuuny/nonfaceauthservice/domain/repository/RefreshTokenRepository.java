package com.hyuuny.nonfaceauthservice.domain.repository;


import com.hyuuny.nonfaceauthservice.common.code.RefreshTokenType;
import com.hyuuny.nonfaceauthservice.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTypeAndRefIdAndIssueId(RefreshTokenType type, Long refId, String issueId);

    void deleteByTypeAndRefId(RefreshTokenType type, Long refId);

}
