package com.katz.licensingservice.services;

import com.katz.licensingservice.config.Config;
import com.katz.licensingservice.external.OrganizationDiscoveryClient;
import com.katz.licensingservice.model.License;
import com.katz.licensingservice.model.Organization;
import com.katz.licensingservice.repository.LicenseRepository;

import java.util.List;
import java.util.UUID;

public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final Config config;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    public LicenseService(LicenseRepository licenseRepository, Config config, OrganizationDiscoveryClient organizationDiscoveryClient) {
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
    }

    public List<License> getLicensesByOrgId(String organizationId) {
        return licenseRepository.findByOrganizationId(organizationId);
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
                .withOrganizationEmail(organization.getOrganizationEmail())
                .withOrganizationName(organization.getName())
                .withOrganizationPhone(organization.getOrganizationPhone());
    }

    private Organization getOrganization(String organizationId, String consumerType) {
        switch (consumerType) {
            case "DiscoveryClient":
                return organizationDiscoveryClient.getOrganization(organizationId);
            case "Feign":
            case "Other":
            default:
                return null;
        }
    }

}
