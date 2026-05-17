package com.nsalexamy.spring_gateway.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "OIDC user profile claims, or an error when no OIDC principal is present")
public class UserProfileResponse {

    @Schema(description = "OIDC subject identifier", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String sub;

    @JsonProperty("preferred_username")
    @Schema(description = "Preferred username from the identity provider", example = "john.doe")
    private String preferredUsername;

    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @JsonProperty("given_name")
    @Schema(description = "User given (first) name", example = "John")
    private String givenName;

    @JsonProperty("family_name")
    @Schema(description = "User family (last) name", example = "Doe")
    private String familyName;

    @JsonProperty("email_verified")
    @Schema(description = "Whether the email address has been verified", example = "true")
    private Boolean emailVerified;

    @Schema(description = "Error message when the OIDC user principal is missing",
            example = "No id_token found")
    private String error;

    @JsonProperty("id_token")
    @Schema(description = "Raw ID token value; null when no OIDC user is present")
    private String idToken;

    public static UserProfileResponse fromOidcUser(OidcUser oidcUser) {
        if (oidcUser == null) {
            return UserProfileResponse.builder()
                    .error("No id_token found")
                    .idToken(null)
                    .build();
        }
        return UserProfileResponse.builder()
                .sub(oidcUser.getSubject())
                .preferredUsername(oidcUser.getPreferredUsername())
                .email(oidcUser.getEmail())
                .givenName(oidcUser.getGivenName())
                .familyName(oidcUser.getFamilyName())
                .emailVerified(oidcUser.getEmailVerified())
                .build();
    }
}
