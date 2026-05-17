package com.nsalexamy.spring_gateway.dto.response;

import com.nsalexamy.spring_gateway.dto.request.CreateUserRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after a user is created (idempotent endpoint)")
public class CreateUserResponse {

    @Schema(description = "Server-generated unique identifier for the created user",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private String id;

    @Schema(description = "Creation status", example = "CREATED")
    private String status;

    @Schema(description = "Echo of the submitted user data")
    private CreateUserRequest data;
}
