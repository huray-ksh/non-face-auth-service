package com.hyuuny.nonfaceauthservice.application.dto;

import com.hyuuny.nonfaceauthservice.domain.entity.ExchangeCode;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class ExchangeCodeDto {

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Create {

        @NotNull
        private String code;

        @NotNull
        private Long userId;

        public ExchangeCode toEntity() {
            return ExchangeCode.builder()
                    .code(this.code)
                    .userId(this.userId)
                    .build();
        }

    }

    @Builder
    @Setter
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @NotNull
        private String code;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;

        @NotNull
        private String code;

        @NotNull
        private Long userId;

        public Response(ExchangeCode entity) {
            this.id = entity.getId();
            this.code = entity.getCode();
            this.userId = entity.getUserId();
        }

    }

}
