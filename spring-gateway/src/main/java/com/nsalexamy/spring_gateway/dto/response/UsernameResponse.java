package com.nsalexamy.spring_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authenticated username from the security context")
public class UsernameResponse {

    @Schema(description = "Username of the authenticated principal", example = "john.doe")
    private String username;
}
