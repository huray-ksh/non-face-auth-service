package com.hyuuny.nonfaceauthservice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class AuthenticationDto {

    @Getter
    @NoArgsConstructor
    public static class Credential {

        private String username;

        private String password;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Data implements Serializable {

        private UserWithToken data;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class UserWithToken implements Serializable {

        private String additionalMessage;

        private AccessToken accessToken;

        private UserInfo userInfo;

        public UserWithToken(AccessToken accessToken, UserInfo userInfo) {
            this(null, accessToken, userInfo);
        }

        public UserWithToken(String additionalMessage, AccessToken accessToken, UserInfo userInfo) {
            this.additionalMessage = additionalMessage;
            this.accessToken = accessToken;
            this.userInfo = userInfo;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessToken implements Serializable {

        private String accessToken;

        private String refreshToken;

        private LocalDateTime accessTokenExpiredAt;

        private LocalDateTime refreshTokenExpiredAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_EMPTY)
    public static class UserInfo implements Serializable {

        private Long id;

        private String name;

        private Account.Status status;

        private Gender gender;

        private String username;

        private String roleType;

        private List<AccountAuthProviderDto> authProviders;

        private String mobilePhoneNumber;

        private LocalDateTime lastLoginAt;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime createdAt;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime updatedAt;

        public UserInfo(AccountAdapter entity) {
            this.id = entity.getUserId();
            this.name = entity.getName();
            this.status = entity.getStatus();
            this.gender = entity.getGender();
            this.username = entity.getUsername();
            this.roleType = entity.getRoleType();
            this.authProviders = entity.getAccountAuthProviders().stream()
                    .map(AccountAuthProviderDto::new)
                    .toList();
            this.lastLoginAt = entity.getLastLoginAt();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }

    }

}
