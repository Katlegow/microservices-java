package com.katz.licensingservice.services;

import com.katz.licensingservice.config.Config;
import com.katz.licensingservice.external.OrganizationDiscoveryClient;
import com.katz.licensingservice.external.OrganizationFeignClient;
import com.katz.licensingservice.external.OrganizationRestTemplateClient;
import com.katz.licensingservice.model.License;
import com.katz.licensingservice.model.Organization;
import com.katz.licensingservice.repository.LicenseRepository;
import com.katz.licensingservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public class LicenseService {
    public static Logger log = LoggerFactory.getLogger(LicenseService.class);
    private final LicenseRepository licenseRepository;
    private final Config config;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;
    private final OrganizationRestTemplateClient organizationRestTemplateClient;
    private final OrganizationFeignClient organizationFeignClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public LicenseService(LicenseRepository licenseRepository, Config config, OrganizationDiscoveryClient organizationDiscoveryClient,
                          OrganizationRestTemplateClient organizationRestTemplateClient, OrganizationFeignClient organizationFeignClient,
                          CircuitBreakerFactory circuitBreakerFactory) {
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
        this.organizationRestTemplateClient = organizationRestTemplateClient;
        this.organizationFeignClient = organizationFeignClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public List<License> getLicensesByOrgId(String organizationId) {

        return circuitBreakerFactory
                .create("licensesByOrgId")
                .run(
                        () -> {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            log.info(
                                    "License Service Correlation Id: {}",
                                    UserContextHolder.get().getCorrelationId()
                            );

                            return licenseRepository.findByOrganizationId(organizationId);
                        },
                        throwable -> {
                                List<License> licenses = new ArrayList<>();
                                licenses.add(
                                        new License()
                                                .withOrganizationId(organizationId)
                                                .withComment("Sorry licenses are not available at the moment!")
                                                .withContactName("Unknown")
                                                .withOrganizationPhone("0000-0000-0000"));
                                return licenses;
                        });
    }

    public License getLicenseByIdAndOrgId(String licenseId, String organizationId) {
        return licenseRepository.findByIdAndOrganizationId(licenseId, organizationId).withComment(config.getTracerProperty());
    }

    public String saveLicense(License license) {
        license.withId(UUID.randomUUID().toString());
        return licenseRepository.save(license).getId();
    }

    public License getLicenseByIdAndOrgId(String licenseId, String organizationId, String consumerType) {
        License license = licenseRepository.findById(licenseId).get();
        Organization organization = getOrganization(organizationId, consumerType);
        return license
                .withContactName(organization.getContactName())
                .withOrganizationEmail(organization.getContactEmail())
                .withOrganizationName(organization.getName())
                .withOrganizationPhone(organization.getContactPhone());
    }

    private Organization getOrganization(String organizationId, String consumerType) {
        switch (consumerType) {
            case "DiscoveryClient":
                return organizationDiscoveryClient.getOrganization(organizationId);
            case "RestTemplateClient":
                return organizationRestTemplateClient.getOrganizationById(organizationId);
            case "FeignClient":
                return organizationFeignClient.getOrgById(organizationId);
            default:
                return null;
        }
    }

}
