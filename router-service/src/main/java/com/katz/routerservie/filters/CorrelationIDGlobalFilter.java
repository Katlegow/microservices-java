package com.katz.routerservie.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.katz.routerservie.definitions.Definitions.TMX_CORRELATION_ID;

@Component
public class CorrelationIDGlobalFilter implements GlobalFilter {
    Logger log = LoggerFactory.getLogger(CorrelationIDGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!exchange.getRequest().getHeaders().containsKey(TMX_CORRELATION_ID)) {
            log.info("Pre filter");
            exchange
                    .getRequest()
                    .mutate()
                    .headers( h -> h.add(
                    TMX_CORRELATION_ID, generateID()
            ));
        }
        return chain
                .filter(exchange);

    }

    private String generateID() {
        return UUID.randomUUID().toString();
    }
}
