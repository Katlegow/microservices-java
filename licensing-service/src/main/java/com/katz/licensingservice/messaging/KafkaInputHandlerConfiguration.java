package com.katz.licensingservice.messaging;

import com.katz.licensingservice.model.OrganisationEventEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaInputHandlerConfiguration {
    Logger log = LoggerFactory.getLogger(KafkaInputHandlerConfiguration.class);

    @Bean
    public Consumer<OrganisationEventEmitter> organizationChangeEventHandler() {
        return (event) -> log.info("Action Emitted: {}", event);
    }
}
