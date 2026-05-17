package com.nsalexamy.spring_resource_server.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String CLIENT_ID = "nsa2-gateway"; // Your Keycloak client ID
    private static final String ROLES = "roles";

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();


    @Override
    public <U> Converter<Jwt, U> andThen(Converter<? super Collection<GrantedAuthority>, ? extends U> after) {
        return Converter.super.andThen(after);
    }
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Collection<GrantedAuthority> authorities = defaultGrantedAuthoritiesConverter.convert(source);
        log.info("authorities : {}", authorities);

        var roles = source.getClaimAsStringList("roles");
        log.info("roles: {}", roles);


        Map<String, Object> resourceAccess = source.getClaimAsMap(RESOURCE_ACCESS);

        if (resourceAccess != null && resourceAccess.containsKey(CLIENT_ID)) {
            Object clientAccessValue = resourceAccess.get(CLIENT_ID);
            if (clientAccessValue instanceof Map<?, ?> clientAccess) {
                Object rolesValue = clientAccess.get(ROLES);
                if (rolesValue instanceof List<?> rolesList) {
                    List<String> clientRoles = rolesList.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .toList();
                    authorities = Stream.concat(
                            authorities.stream(),
                            clientRoles.stream()
                                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                                    .map(SimpleGrantedAuthority::new)
                    ).collect(Collectors.toList());
                }
            }
        }

        log.info("authorities : {}", authorities);

        return authorities;
    }

}
