package com.hyuuny.nonfaceauthservice.common.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ResponseCode {

    // 2xx
    SUCCESS(200_00, "OK"),
    SUCCESS_BUT_REQUIRED_CHANGE_PASSWORD(200_01, "SUCCESS_BUT_REQUIRED_CHANGE_PASSWORD"),
    CREATED(201_00, "CREATED"),
    NO_CONTENT(204_00, "NO CONTENT"),
    // 4xx
    BAD_REQUEST(400_00, "BAD REQUEST"),
    APPLICANT_NOT_MATCHED(400_10, "APPLICANT_NOT_MATCHED"),
    NOT_APPLICANT(400_20, "NOT_APPLICANT"),
    APPLICANT_REGISTRATION_IN_PROGRESS(400_30, "APPLICANT_REGISTRATION_IN_PROGRESS"),
    UNAUTHORIZED(401_00, "UNAUTHORIZED"),
    NO_MATCHED_USERNAME(401_01, "NO MATCHED USERNAME"),
    NO_MATCHED_PASSWORD(401_02, "NO MATCHED PASSWORD"),
    FORBIDDEN(403_00, "FORBIDDEN"),
    NO_VALID_AUTHORIZATION(403_01, "NO VALID AUTHORIZATION"),
    NO_VALID_STATUS_REGISTERED(403_02, "NO_VALID_STATUS_REGISTERED"),
    NO_VALID_STATUS_LOCKED(403_03, "NO_VALID_STATUS_LOCKED"),
    NO_VALID_STATUS_DELETED(403_04, "NO_VALID_STATUS_DELETED"),
    NO_VALID_STATUS_SET_PROFILE(403_05, "NO_VALID_STATUS_SET_PROFILE"),
    NO_VALID_STATUS_EXECUTE_SURVEY(403_06, "NO_VALID_STATUS_EXECUTE_SURVEY"),
    NO_VALID_STATUS_IN_SURVEY(403_07, "NO_VALID_STATUS_IN_SURVEY"),
    NO_VALID_STATUS_SET_GOAL(403_08, "NO_VALID_STATUS_SET_GOAL"),
    NO_VALID_STATUS_IN_SET_GOAL(403_09, "NO_VALID_STATUS_IN_SET_GOAL"),
    NO_VALID_STATUS_DORMANCY(403_10, "NO_VALID_STATUS_DORMANCY"),
    EXCEED_FAILED_PASSWORD_MATCH(403_15, "EXCEED_FAILED_PASSWORD_MATCH"),
    NOT_FOUND(404_00, "NOT FOUND"),
    CONFLICT(409_00, "CONFLICT"),
    // 5xx
    UNKNOWN(500_00, "INTERNAL SERVER ERROR"),
    SERVICE_INITIALIZATION_FAILED(500_01, "SERVICE_INITIALIZATION_FAILED"),
    CREDENTIAL_GENERATION_EXCEPTION(510_00, "CREDENTIAL_GENERATION_EXCEPTION"),
    INTEGRATION_NCP_OBJECT_STORAGE(520_00, "INTEGRATION_NCP_OBJECT_STORAGE")
    ;

    static final Map<Integer, ResponseCode> lookup = new HashMap<>();
    static {
        for(ResponseCode current : ResponseCode.values()) {
            lookup.put(current.code, current);
        }
    }

    Integer code;
    String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseCode getPolarxResponseCode(Integer code) {
        return lookup.get(code);
    }

    public static ResponseCode findByHttpStatus(HttpStatus status) {
        int code = status.value() * 100;
        return lookup.get(code);
    }

    public static HttpStatus findByChronicResponseCode(ResponseCode responseCode) {
        int code = responseCode.code / 100;
        return HttpStatus.valueOf(code);
    }

}
