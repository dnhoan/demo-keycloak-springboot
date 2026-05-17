package com.nsalexamy.spring_resource_server.exception;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestSecurityExceptionHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        writeError(response, request, authException);
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        writeError(response, request, accessDeniedException);
    }

    private void writeError(HttpServletResponse response, HttpServletRequest request, Exception ex)
            throws IOException {
        var entity = ex instanceof AccessDeniedException
                ? GlobalExceptionHandler.errorResponse(ErrorCode.ACCESS_DENIED, ex.getMessage(), request)
                : GlobalExceptionHandler.errorResponse(ErrorCode.UNAUTHORIZED, ex.getMessage(), request);

        response.setStatus(entity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), entity.getBody());
    }
}
