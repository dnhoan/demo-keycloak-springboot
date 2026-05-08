package com.nsalexamy.spring_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
