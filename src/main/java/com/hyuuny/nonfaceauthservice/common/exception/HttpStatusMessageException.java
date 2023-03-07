package com.hyuuny.nonfaceauthservice.common.exception;

import com.hyuuny.nonfaceauthservice.common.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class HttpStatusMessageException extends ResponseStatusException {

    private Integer code;

    private String message;

    public HttpStatusMessageException(Exception e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
        this.code = ResponseCode.UNKNOWN.getCode();
        this.message = e.getMessage();
    }

    public HttpStatusMessageException(HttpStatus status) {
        super(status);
        ResponseCode responseCode = ResponseCode.findByHttpStatus(status);
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public HttpStatusMessageException(HttpStatus status, String message) {
        super(status);
        ResponseCode responseCode = ResponseCode.findByHttpStatus(status);
        this.code = responseCode.getCode();
        this.message = message;
    }

    public HttpStatusMessageException(ResponseCode responseCode) {
        super(ResponseCode.findByChronicResponseCode(responseCode));
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public HttpStatusMessageException(ResponseCode responseCode, String errorMessage) {
        super(ResponseCode.findByChronicResponseCode(responseCode));
        this.code = responseCode.getCode();
        this.message = errorMessage != null ? errorMessage : responseCode.getMessage();
    }

    public HttpStatusMessageException(String errorMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
        this.code = ResponseCode.UNKNOWN.getCode();
        this.message = errorMessage != null ? errorMessage : ResponseCode.UNKNOWN.getMessage();
    }

}
