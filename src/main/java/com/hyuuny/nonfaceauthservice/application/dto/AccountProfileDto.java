package com.hyuuny.nonfaceauthservice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hyuuny.nonfaceauthservice.domain.entity.AccountProfile;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AccountProfileDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_EMPTY)
    public static class Response {

        @NotNull
        private Long id;

        private String email;

        private String mobilePhoneNumber;

        private LocalDateTime lastLoginAt;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime createdAt;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime updatedAt;

        public Response(AccountProfile entity) {
            this.id = entity.getId();
            this.email = entity.getUsername();
            this.mobilePhoneNumber = entity.getMobilePhoneNumber();
            this.lastLoginAt = entity.getLastLoginAt();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }

    }

}
