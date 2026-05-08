package com.nsalexamy.spring_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

@Configuration
public class GatewayConfig {

    @Value("${RESOURCE_SERVER_URI:http://localhost:8082}")
    private String resourceServerUri;

    @Bean
    public RouterFunction<ServerResponse> resourceServerRoute() {
        return GatewayRouterFunctions.route("resource-server")
                .GET("/resource/**", HandlerFunctions.http())
                .POST("/resource/**", HandlerFunctions.http())
                .PUT("/resource/**", HandlerFunctions.http())
                .DELETE("/resource/**", HandlerFunctions.http())
                .before(BeforeFilterFunctions.stripPrefix(1))
                .before(BeforeFilterFunctions.uri(URI.create(resourceServerUri)))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build();
    }
}
