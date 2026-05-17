package com.nsalexamy.spring_gateway.aspect;

import com.nsalexamy.spring_gateway.annotation.Idempotent;
import com.nsalexamy.spring_gateway.dto.response.IdempotencyErrorResponse;
import com.nsalexamy.spring_gateway.model.IdempotentResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class IdempotencyAspect {

    // In-memory store: idempotency-key -> cached response
    private final Map<String, IdempotentResponse> cache = new ConcurrentHashMap<>();

    @Around("@annotation(com.nsalexamy.spring_gateway.annotation.Idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        String idempotencyKey = request.getHeader(idempotent.headerName());

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            log.warn("Missing {} header on {}", idempotent.headerName(), request.getRequestURI());
            if (response != null) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
            return new IdempotencyErrorResponse(
                    "Missing idempotency key",
                    idempotent.headerName()
            );
        }

        // Evict expired entry if present
        IdempotentResponse cached = cache.get(idempotencyKey);
        if (cached != null && cached.isExpired()) {
            cache.remove(idempotencyKey);
            cached = null;
        }

        if (cached != null) {
            log.info("Idempotency cache hit for key: {}", idempotencyKey);
            if (response != null) {
                response.setHeader("X-Idempotency-Status", "HIT");
            }
            return cached.getBody();
        }

        // Execute the actual handler
        Object result = joinPoint.proceed();

        cache.put(idempotencyKey, new IdempotentResponse(result, Instant.now(), idempotent.ttlSeconds()));
        log.info("Idempotency response cached for key: {}", idempotencyKey);

        if (response != null) {
            response.setHeader("X-Idempotency-Status", "MISS");
        }

        return result;
    }
}
