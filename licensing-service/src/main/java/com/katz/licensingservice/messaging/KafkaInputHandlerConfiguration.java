package com.katz.licensingservice.messaging;

import com.katz.licensingservice.cache.OrganizationRedisOperation;
import com.katz.licensingservice.model.OrganisationEventEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaInputHandlerConfiguration {
    Logger log = LoggerFactory.getLogger(KafkaInputHandlerConfiguration.class);

    private final OrganizationRedisOperation organizationRedisOperation;

    @Autowired
    public KafkaInputHandlerConfiguration(OrganizationRedisOperation organizationRedisOperation) {
        this.organizationRedisOperation = organizationRedisOperation;
    }

    @Bean
    public Consumer<OrganisationEventEmitter> organizationChangeEventHandler() {
        return (event) -> {
            log.info("Action Emitted: {}", event);
            switch (event.getAction()) {
                case "UPDATE":
                    log.info(
                            "Received a UPDATE event and then react accordingly. from the organization service for organization id {}|" +
                            "Transaction id {}",
                            event.getOrganizationId(),
                            event.getCorrelationId()
                    );
                    organizationRedisOperation.deleteOrganization(event.getOrganizationId());
                break;
                case "DELETE":
                    log.info(
                            "Received a DELETE event and then react accordingly. from the organization service for organization id {}|" +
                            "Transaction id {}",
                            event.getOrganizationId(),
                            event.getCorrelationId()
                    );
                    organizationRedisOperation.deleteOrganization(event.getOrganizationId());
                    break;
                case "ADD":
                    log.info(
                            "Received a ADD event and then react accordingly. from the organization service for organization id {}|" +
                            "Transaction id {}",
                            event.getOrganizationId(),
                            event.getCorrelationId()
                    );
                    break;
                default:
                    log.info(
                            "Received UNKNOWN EVENT from the organization service for organization id {}|" + "Transaction id {}",
                            event.getOrganizationId(),
                            event.getCorrelationId()
                    );
            }
        };
    }
}
