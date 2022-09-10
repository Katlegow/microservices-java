package com.katz.routerservie;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public RouteLocator configureRoute(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route("licenseId",
                        route -> route
                                .path("/v1/organization/**")
                                .uri("lb://LICENSINGSERVICE")           // Dynamic routing

                ).route("organizationId",
                        route -> route
                                .path("/v1/organizations/**")
                                .uri("http://localhost:8080")          // Static routing, one instance in this case hence no lb:/
                ).build();
    }
}
