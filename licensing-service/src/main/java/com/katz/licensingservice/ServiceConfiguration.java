package com.katz.licensingservice;

import com.katz.licensingservice.config.Config;
import com.katz.licensingservice.external.OrganizationDiscoveryClient;
import com.katz.licensingservice.external.OrganizationFeignClient;
import com.katz.licensingservice.external.OrganizationRestTemplateClient;
import com.katz.licensingservice.repository.LicenseRepository;
import com.katz.licensingservice.services.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceConfiguration {

    private final LicenseRepository licenseRepository;
    private final Config config;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;
    private final OrganizationRestTemplateClient organizationRestTemplateClient;
    private final OrganizationFeignClient organizationFeignClient;

    @Autowired
    public ServiceConfiguration(LicenseRepository licenseRepository, Config config, OrganizationDiscoveryClient organizationDiscoveryClient,
                                OrganizationRestTemplateClient organizationRestTemplateClient, OrganizationFeignClient organizationFeignClient) {
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
        this.organizationRestTemplateClient = organizationRestTemplateClient;
        this.organizationFeignClient = organizationFeignClient;
    }

    @Bean
    public LicenseService getLicenseService() {
        return new LicenseService(licenseRepository, config, organizationDiscoveryClient, organizationRestTemplateClient,
                organizationFeignClient);
    }


    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
