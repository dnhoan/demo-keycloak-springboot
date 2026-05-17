package com.nsalexamy.spring_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error returned when a required idempotency header is missing")
public class IdempotencyErrorResponse {

    @Schema(description = "Human-readable error message", example = "Missing idempotency key")
    private String error;

    @Schema(description = "Name of the required request header", example = "Idempotency-Key")
    private String header;
}
