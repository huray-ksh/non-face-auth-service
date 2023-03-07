package com.hyuuny.nonfaceauthservice.application;

import com.hyuuny.nonfaceauthservice.common.code.RefreshTokenType;
import com.hyuuny.nonfaceauthservice.common.exception.HttpStatusMessageException;
import com.hyuuny.nonfaceauthservice.domain.entity.RefreshToken;
import com.hyuuny.nonfaceauthservice.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public void verifyUserRefreshToken(RefreshTokenType type, Long refId, String issueId) {
        refreshTokenRepository.findByTypeAndRefIdAndIssueId(type, refId, issueId).orElseThrow(
                () -> new HttpStatusMessageException(HttpStatus.UNAUTHORIZED, refId + "의 refreshToken을 찾을 수 없습니다.")
        );
    }

    @Transactional
    public void deleteRefreshToken(RefreshTokenType type, Long refId) {
        refreshTokenRepository.deleteByTypeAndRefId(type, refId);
    }

}
