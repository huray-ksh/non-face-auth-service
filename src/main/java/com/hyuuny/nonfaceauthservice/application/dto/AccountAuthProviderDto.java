package com.hyuuny.nonfaceauthservice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hyuuny.nonfaceauthservice.common.constants.AuthProvider;
import com.hyuuny.nonfaceauthservice.domain.entity.AccountAuthProvider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class AccountAuthProviderDto {

    @NotNull
    private AuthProvider provider;

    @NotEmpty
    private String providerUserIdentifier;

    public AccountAuthProvider toEntity() {
        return AccountAuthProvider.builder()
                .provider(this.provider)
                .providerUserIdentifier(this.providerUserIdentifier)
                .build();
    }

    public AccountAuthProviderDto(AccountAuthProvider entity) {
        this.provider = entity.getProvider();
        this.providerUserIdentifier = entity.getProviderUserIdentifier();
    }

}
