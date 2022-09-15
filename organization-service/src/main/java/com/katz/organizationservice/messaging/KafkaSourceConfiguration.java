package com.katz.organizationservice.messaging;

import com.katz.organizationservice.model.OrganisationEventEmitter;
import com.katz.organizationservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.BiFunction;

@Configuration
public class KafkaSourceConfiguration {
    Logger log = LoggerFactory.getLogger(KafkaSourceConfiguration.class);

    @Bean
    @Qualifier("OrganizationActionEvenEmitter")
    public BiFunction<String, String, OrganisationEventEmitter> organizationChangeEvent() {

        return (action, orgId) -> new OrganisationEventEmitter(
                OrganisationEventEmitter.class.getTypeName(),
                UserContextHolder.get().getCorrelationId(),
                orgId,
                action
        );
    }

}
