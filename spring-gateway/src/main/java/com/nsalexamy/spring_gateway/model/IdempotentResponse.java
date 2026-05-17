package com.nsalexamy.spring_gateway.model;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class IdempotentResponse {

    private Object body;
    private Instant createdAt;
    private long ttlSeconds;

    public boolean isExpired() {
        return Instant.now().isAfter(createdAt.plusSeconds(ttlSeconds));
    }
}
