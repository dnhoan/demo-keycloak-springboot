package com.nsalexamy.spring_resource_server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNAUTHORIZED("ERR-401", HttpStatus.UNAUTHORIZED, "Unauthorized"),
    ACCESS_DENIED("ERR-403", HttpStatus.FORBIDDEN, "Access denied"),
    VALIDATION_FAILED("ERR-400-VALIDATION", HttpStatus.BAD_REQUEST, "Validation failed"),
    BAD_REQUEST("ERR-400", HttpStatus.BAD_REQUEST, "Bad request"),
    NOT_FOUND("ERR-404", HttpStatus.NOT_FOUND, "Not found"),
    INTERNAL_SERVER_ERROR("ERR-500", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final String code;
    private final HttpStatus status;
    private final String error;
}
