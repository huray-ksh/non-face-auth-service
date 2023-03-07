package com.hyuuny.nonfaceauthservice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.Lists;
import com.hyuuny.nonfaceauthservice.application.dto.oauth.ProviderUser;
import com.hyuuny.nonfaceauthservice.common.constants.AuthProvider;
import com.hyuuny.nonfaceauthservice.domain.entity.*;
import com.hyuuny.nonfaceauthservice.domain.entity.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder.Default;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.hyuuny.nonfaceauthservice.domain.entity.Privilege.Type.*;
import static com.hyuuny.nonfaceauthservice.domain.entity.Role.Type.ROLE_SYS_USER;
import static org.springframework.util.ObjectUtils.isEmpty;

public class AccountDto {

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SignUpRequest {

        private String email;

        private String password;

        private String name;

        private String mobilePhoneNumber;

        private Gender gender;

        private String thumbnailUrl;

        private Role.Type roleType;

        @Default
        private List<Privilege.Type> privilegeTypes = Lists.newArrayList();

        @Valid
        private AccountAuthProviderDto authProvider;

        public Account toEntity() {
            Account newAccount = Account.builder()
                    .name(this.name)
                    .gender(this.gender)
                    .thumbnailUrl(this.thumbnailUrl)
                    .accountProfile(AccountProfile.builder()
                            .username(this.email)
                            .password(this.password)
                            .mobilePhoneNumber(this.mobilePhoneNumber)
                            .build())
                    .build();

            if (!isEmpty(this.authProvider)) {
                AccountAuthProvider provider = this.authProvider.toEntity();
                newAccount.addAuthProvider(provider);
            }
            return newAccount;
        }

        public static SignUpRequest toAuthSignUpRequest(ProviderUser providerUser, String registrationId) {
            return SignUpRequest.builder()
                    .email(providerUser.getUsername())
                    .password(providerUser.getPassword())
                    .name(providerUser.getName())
                    .mobilePhoneNumber(providerUser.getMobilePhoneNumber())
                    .gender(providerUser.getGender())
                    .roleType(ROLE_SYS_USER)
                    .privilegeTypes(Lists.newArrayList(
                            PRIVILEGE_USER_C,
                            PRIVILEGE_USER_R,
                            PRIVILEGE_USER_U,
                            PRIVILEGE_USER_D
                    ))
                    .authProvider(AccountAuthProviderDto.builder()
                            .provider(AuthProvider.valueOf(registrationId.toUpperCase()))
                            .providerUserIdentifier(providerUser.getId())
                            .build())
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_EMPTY)
    public static class Response {

        @NotNull
        private Long id;

        @NotNull
        private UUID uid;

        @NotNull
        private Account.Status status;

        @NotNull
        private String name;

        private String thumbnailUrl;

        @NotNull
        private AccountProfileDto.Response accountProfile;

        @NotNull
        private RoleDto.Response role;

        private List<AccountAuthProviderDto> authProvider;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime createdAt;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime updatedAt;

        public Response(Account entity) {
            this.id = entity.getId();
            this.uid = entity.getUid();
            this.status = entity.getStatus();
            this.name = entity.getName();
            this.thumbnailUrl = entity.getThumbnailUrl();
            this.accountProfile = new AccountProfileDto.Response(entity.getAccountProfile());
            this.role = new RoleDto.Response(entity.getRole());
            this.authProvider = entity.getAuthProviders().stream()
                    .map(AccountAuthProviderDto::new)
                    .toList();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }

}
