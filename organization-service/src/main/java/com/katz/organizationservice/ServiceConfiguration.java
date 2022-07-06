package com.katz.organizationservice;

import com.katz.organizationservice.repository.OrganizationRepository;
import com.katz.organizationservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {
    private final OrganizationRepository repository;

    @Autowired
    public ServiceConfiguration(OrganizationRepository repository) {
        this.repository = repository;
    }

    @Bean
    public OrganizationService getOrganizationService() {
        return new OrganizationService(repository);
    }
}
