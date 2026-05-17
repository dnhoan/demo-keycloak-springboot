package com.nsalexamy.spring_gateway.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new user")
public class CreateUserRequest {

    @Schema(description = "Unique login name for the user", example = "john.doe")
    private String username;

    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User given (first) name", example = "John")
    private String firstName;

    @Schema(description = "User family (last) name", example = "Doe")
    private String lastName;
}
