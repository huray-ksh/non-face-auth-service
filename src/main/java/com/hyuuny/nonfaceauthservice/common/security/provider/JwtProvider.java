package com.hyuuny.nonfaceauthservice.common.security.provider;

import com.google.common.collect.Maps;
import com.hyuuny.nonfaceauthservice.application.RefreshTokenService;
import com.hyuuny.nonfaceauthservice.application.dto.AccountAdapter;
import com.hyuuny.nonfaceauthservice.application.dto.AuthenticationDto;
import com.hyuuny.nonfaceauthservice.common.code.ResponseCode;
import com.hyuuny.nonfaceauthservice.common.constants.AuthResourceAppConstant;
import com.hyuuny.nonfaceauthservice.common.exception.HttpStatusMessageException;
import com.hyuuny.nonfaceauthservice.domain.entity.RefreshToken;
import com.hyuuny.nonfaceauthservice.domain.service.AccountDomainService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.ZoneId;
import java.util.*;

import static com.hyuuny.nonfaceauthservice.common.code.RefreshTokenType.SERVICE_APP;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.token.secret}")
    public String tokenSecret;
    @Value("${jwt.token.duration.access-token}")
    public Long accessTokenValidDuration;
    @Value("${jwt.token.duration.refresh-token}")
    public Long refreshTokenValidDuration;
    @Value("${non-face-auth-service.service.name}")
    public String serviceName;

    private final AccountDomainService accountDomainService;

    private final RefreshTokenService refreshTokenService;

//    private final RedisService redisService;

    private Key key;

    @PostConstruct
    public void init() {
        String realBaseSecret = this.tokenSecret
                + "0123456789abcdefghijklmnopqrstuvwzyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                + this.tokenSecret;
        String secret = Base64.getEncoder().encodeToString(realBaseSecret.getBytes());
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Authentication verifyAccessToken(String jwtToken) {
        return this.verifyAccessToken(jwtToken, Scope.ACCESS);
    }

    public Authentication verifyAccessToken(String jwtToken, Scope scope) {
        Claims claims = this.verifyToken(jwtToken, scope);
        String userIdString = claims.getSubject();
        log.debug("####### access user = {}", userIdString);

        if (isEmpty(userIdString)) {
            log.debug("####### Bad token, subject id is null");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        AccountAdapter userDetail = accountDomainService.getUserDetails(Long.valueOf(userIdString));
        return new UsernamePasswordAuthenticationToken(
                userDetail,
                userDetail.getPassword(),
                userDetail.getAuthorities()
        );
    }

    public void verifyRefreshToken(String jwtToken) {
        this.verifyToken(jwtToken, Scope.REFRESH);
    }

//    private AccountAdapter getUserDetails(String userIdString) {
//        String formattedRedisUserKey = getRedisUserKey(userIdString);
//        Object userDetailObject = redisService.getObject(formattedRedisUserKey);
//        if (!(userDetailObject instanceof AccountAdapter)) {
//            AccountAdapter userDetail = userDomainService.getUserDetails(Long.valueOf(userIdString));
//            redisService.setObject(formattedRedisUserKey, userDetail, (accessTokenValidDuration - 1000));
//            return userDetail;
//        }
//        log.debug("####### Redis object = {}", userDetailObject);
//        return (AccountAdapter) userDetailObject;
//    }

    private String getRedisUserKey(String userIdString) {
        String redisUserKey = AuthResourceAppConstant.CRATE_AUTH_RESOURCE_APP_KEY_USER;
        redisUserKey = redisUserKey.formatted(serviceName);
        return redisUserKey.concat(userIdString);
    }

    public AuthenticationDto.UserWithToken issueLoginToken(Long userId) {
        AccountAdapter userDetails = accountDomainService.getUserDetails(userId);
        Date now = new Date();
        Date expirationDate = getExpiredDate(now, Scope.ACCESS);
        Date refreshExpirationDate = getExpiredDate(now, Scope.REFRESH);

        String accessTokenString = getTokenString(userId, Scope.ACCESS, now, expirationDate);
        String refreshTokenString = getTokenString(userId, Scope.REFRESH, now, refreshExpirationDate);
        return AuthenticationDto.UserWithToken.builder()
                .additionalMessage(null)
                .accessToken(
                        AuthenticationDto.AccessToken.builder()
                                .accessToken(accessTokenString)
                                .refreshToken(refreshTokenString)
                                .accessTokenExpiredAt(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                                .refreshTokenExpiredAt(refreshExpirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                                .build()
                )
                .build();
    }

    private String getTokenString(Long userId, Scope scope, Date now, Date expirationDate) {
        String issuer = UUID.randomUUID().toString();
        HashMap<String, Object> claims = Maps.newHashMap();
        claims.put("scope", scope.text);
        String tokenString = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(this.key)
                .addClaims(claims)
                .compact();

        if (scope.isRefresh()) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .type(SERVICE_APP)
                    .refId(userId)
                    .issueId(issuer)
                    .refreshToken(tokenString)
                    .issuedAt(now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();
            refreshTokenService.addRefreshToken(refreshToken);
        }
        return tokenString;
    }

    private Date getExpiredDate(Date now, Scope scope) {
        long currentTimeMilli = now.getTime();
        return scope.isAccess()
                ? new Date(currentTimeMilli + accessTokenValidDuration)
                : new Date(currentTimeMilli + refreshTokenValidDuration);
    }

    public AuthenticationDto.UserWithToken reissueLoginToken(String refreshToken) {
        AuthenticationDto.UserWithToken response;
        try {
            Claims body = validateSignedTokenAndGetClaims(refreshToken);
            String userIdString = body.getSubject();
            String issuerId = body.getIssuer();
            refreshTokenService.verifyUserRefreshToken(SERVICE_APP, Long.valueOf(userIdString), issuerId);

            response = issueLoginToken(Long.valueOf(userIdString));
        } catch (Exception e) {
            throw new HttpStatusMessageException(ResponseCode.UNAUTHORIZED, "refreshToken 이 유효하지 않습니다.");
        }
        return response;
    }

    public void deleteRefreshToken(String refreshToken) {
        Claims body = this.validateSignedTokenAndGetClaims(refreshToken);
        String userIdString = body.getSubject();
        refreshTokenService.deleteRefreshToken(SERVICE_APP, Long.valueOf(userIdString));
    }

    public Claims verifyToken(@NotEmpty String jwtToken, Scope scope) {
        // 토큰 검사
        try {
            // 토큰 만료여부 검사
            // 만료된 토큰이면 ExpiredJwtException 발생
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(jwtToken);

            Claims body = claims.getBody();
            String scopeValue = Optional.ofNullable((String) body.get("scope"))
                    .orElseThrow(() -> new MalformedJwtException("not allowed token"));

            log.debug("jwt verify {}: {}", scopeValue, claims);

            // 토큰 유형 검사
            if (!scopeValue.equals(scope.text)) {
                throw new AccessDeniedException("Invalid token scope.");
            }

            return this.validateSignedTokenAndGetClaims(jwtToken);
        } catch (Exception e) {
            log.error("[JWT verify Error] errorMsg = {}", e.getMessage());
            throw new HttpStatusMessageException(ResponseCode.UNAUTHORIZED, "token 이 만료되었습니다.");
        }
    }

    public Claims validateSignedTokenAndGetClaims(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build();
        if (!jwtParser.isSigned(token)) {
            throw new HttpStatusMessageException(ResponseCode.UNAUTHORIZED, "accessToken 이 유효하지 않습니다.");
        }
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public enum Scope {
        ACCESS("access"),
        REFRESH("refresh");

        private final String text;

        Scope(final String text) {
            this.text = text;
        }

        public boolean isAccess() {
            return this == ACCESS;
        }

        public boolean isRefresh() {
            return this == REFRESH;
        }

        @Override
        public String toString() {
            return text;
        }
    }

//    public void disableToken(final String jwtToken) {
//        redisService.forceKeyExpiration(jwtToken);
//    }

}
