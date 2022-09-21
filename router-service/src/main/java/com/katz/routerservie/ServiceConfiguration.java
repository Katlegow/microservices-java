package com.katz.routerservie;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

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

    @Bean
    WebFilter traceIdInResponseFilter(Tracer tracer) {
        return (exchange, chain) -> {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                // putting trace id value in [mytraceid] response header
                exchange.getResponse().getHeaders().add("mytraceid", currentSpan.context().traceId());
            }
            return chain.filter(exchange);
        };
    }
}
