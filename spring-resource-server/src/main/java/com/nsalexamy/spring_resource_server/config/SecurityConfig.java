package com.nsalexamy.spring_resource_server.config;

import com.nsalexamy.spring_resource_server.exception.RestSecurityExceptionHandlers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // <1>
public class SecurityConfig {

    private final String jwkSetUri;
    private final RestSecurityExceptionHandlers securityExceptionHandlers;

    public SecurityConfig(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            RestSecurityExceptionHandlers securityExceptionHandlers) {
        this.jwkSetUri = jwkSetUri;
        this.securityExceptionHandlers = securityExceptionHandlers;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter nsa2AuthenticationConverter) throws Exception {

        http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityExceptionHandlers)
                        .accessDeniedHandler(securityExceptionHandlers)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(nsa2AuthenticationConverter) // <3>
                                .jwkSetUri(jwkSetUri)   // <4>
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter nsa2AuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
        return converter;
    }
}