package com.hyuuny.nonfaceauthservice.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyuuny.nonfaceauthservice.common.code.ResponseCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponseDto<T> implements Serializable {

    private Integer code;
    private String message;
    private Long totalPage;
    private Long totalCount;
    private String path;
    private T data;

    // For error
    public ResultResponseDto(Integer code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
    }

    // For success and equivalent
    public ResultResponseDto(@NonNull ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public ResultResponseDto(@NonNull HttpStatus status) {
        this.code = status.value();
        this.message = status.name();
    }

    public ResultResponseDto(@NonNull T data) {
        this.code = ResponseCode.SUCCESS.getCode();
        this.message = ResponseCode.SUCCESS.getMessage();
        this.data = data;
    }

    public ResultResponseDto(@NonNull T data, Long totalPage, Long totalCount) {
        this(ResponseCode.SUCCESS, null, data, totalPage, totalCount);
    }

    public ResultResponseDto(@NonNull ResponseCode responseCode,
                             String message,
                             @NonNull T data,
                             Long totalPage,
                             Long totalCount) {
        this.code = responseCode.getCode();
        this.message = message != null ? message : responseCode.getMessage();
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.data = data;
    }

}
