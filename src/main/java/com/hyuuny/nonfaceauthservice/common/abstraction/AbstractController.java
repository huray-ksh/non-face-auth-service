package com.hyuuny.nonfaceauthservice.common.abstraction;

import com.hyuuny.nonfaceauthservice.common.code.ResponseCode;
import com.hyuuny.nonfaceauthservice.common.dto.ResultResponseDto;
import com.hyuuny.nonfaceauthservice.common.exception.HttpStatusMessageException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AbstractController {

    protected <T> ResponseEntity<ResultResponseDto<T>> noContent() {
        return ResponseEntity.noContent().build();
    }

    protected <T> ResponseEntity<ResultResponseDto<T>> created(T data) {
        ResultResponseDto<T> response = new ResultResponseDto<>(
                ResponseCode.CREATED,
                null,
                data,
                null,
                null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    protected <T> ResponseEntity<ResultResponseDto<T>> ok(@NonNull T data) {
        ResultResponseDto<T> response = new ResultResponseDto<>(data);
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity<ResultResponseDto<T>> okAdditional(
            @NonNull T data,
            @NonNull ResponseCode responseCode,
            String message
    ) {
        ResultResponseDto<T> response = new ResultResponseDto<>(
                responseCode,
                message,
                data,
                null,
                null);
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity<ResultResponseDto<T>> ok(@NonNull T data, Long totalPage, Long totalCount) {
        ResultResponseDto<T> response = new ResultResponseDto<>(data, totalPage, totalCount);
        return ResponseEntity.ok(response);
    }

    protected UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails;
        }
        throw new HttpStatusMessageException(ResponseCode.UNAUTHORIZED, "인증 사용자 정보가 존재하지 않습니다.");
    }

    protected UserDetails getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

}
