package com.nsalexamy.spring_gateway.controller;

import com.nsalexamy.spring_gateway.annotation.Idempotent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    // <1>
    @GetMapping("/username")
    public Map<String, String> username(Authentication authentication) {
        String username = authentication.getName();
        log.info("username: {}",username);
        return Map.of("username", username);
    }

    // <3>
    @Idempotent
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createUser(@RequestBody Map<String, Object> payload) {
        log.info("Creating user with payload: {}", payload);
        String generatedId = UUID.randomUUID().toString();
        return Map.of(
                "id", generatedId,
                "status", "CREATED",
                "data", payload
        );
    }

    // <2>
    @GetMapping("/profile")
    public Map<String, Object> idToken(@AuthenticationPrincipal OidcUser oidcUser) {
        log.info("oidcUser: {}", oidcUser);
        log.info("id token: {}", oidcUser.getIdToken().getTokenValue());

        if(oidcUser == null) {
            return Map.of("error", "No id_token found", "id_token", null);

        } else {
            return oidcUser.getClaims();
        }
    }
}
