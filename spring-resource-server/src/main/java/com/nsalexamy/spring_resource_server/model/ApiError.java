package com.nsalexamy.spring_resource_server.model;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        String code,
        int status,
        String error,
        String message,
        String path
) {
}
