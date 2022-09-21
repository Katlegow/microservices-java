package com.katz.organizationservice;

import com.katz.organizationservice.model.OrganisationEventEmitter;
import com.katz.organizationservice.repository.OrganizationRepository;
import com.katz.organizationservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.BiFunction;

@Configuration
public class ServiceConfiguration {
    private final OrganizationRepository repository;

    private final StreamBridge bridge;
    private final Tracer tracer;

    @Autowired
    public ServiceConfiguration(OrganizationRepository repository, StreamBridge bridge, Tracer tracer) {
        this.repository = repository;
        this.bridge = bridge;
        this.tracer = tracer;
    }

    @Bean
    public OrganizationService getOrganizationService() {
        return new OrganizationService(repository, bridge, tracer);
    }
}
