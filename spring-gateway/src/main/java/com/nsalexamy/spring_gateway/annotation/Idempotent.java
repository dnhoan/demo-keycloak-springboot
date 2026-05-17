package com.nsalexamy.spring_gateway.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    // Header name the client sends. Default: Idempotency-Key
    String headerName() default "Idempotency-Key";

    // How long (seconds) to keep the cached response
    long ttlSeconds() default 86400; // 24 hours
}
