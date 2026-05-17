package com.nsalexamy.spring_gateway.controller;

import com.nsalexamy.spring_gateway.annotation.Idempotent;
import com.nsalexamy.spring_gateway.dto.request.CreateUserRequest;
import com.nsalexamy.spring_gateway.dto.response.CreateUserResponse;
import com.nsalexamy.spring_gateway.dto.response.IdempotencyErrorResponse;
import com.nsalexamy.spring_gateway.dto.response.UserProfileResponse;
import com.nsalexamy.spring_gateway.dto.response.UsernameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Authenticated user endpoints (OAuth2/OIDC)")
public class UserController {

    // <1>
    @Operation(summary = "Get username", description = "Returns the authenticated user's username from the security context")
    @ApiResponse(responseCode = "200", description = "Username retrieved",
            content = @Content(schema = @Schema(implementation = UsernameResponse.class)))
    @GetMapping("/username")
    public UsernameResponse username(Authentication authentication) {
        String username = authentication.getName();
        log.info("username: {}", username);
        return new UsernameResponse(username);
    }

    // <3>
    @Operation(summary = "Create user", description = "Creates a user resource. Idempotent when the same Idempotency-Key is reused.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = CreateUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing Idempotency-Key header",
                    content = @Content(schema = @Schema(implementation = IdempotencyErrorResponse.class)))
    })
    @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, required = true,
            description = "Client-generated unique key per create operation (e.g. UUID)")
    @Idempotent
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse createUser(@RequestBody CreateUserRequest request) {
        log.info("Creating user with payload: {}", request);
        String generatedId = UUID.randomUUID().toString();
        return new CreateUserResponse(generatedId, "CREATED", request);
    }

    // <2>
    @Operation(summary = "Get user profile", description = "Returns OIDC ID token claims for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile claims or error when no OIDC user",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    @GetMapping("/profile")
    public UserProfileResponse idToken(@AuthenticationPrincipal OidcUser oidcUser) {
        log.info("oidcUser: {}", oidcUser);
        if (oidcUser != null) {
            log.info("id token: {}", oidcUser.getIdToken().getTokenValue());
        }
        return UserProfileResponse.fromOidcUser(oidcUser);
    }
}
