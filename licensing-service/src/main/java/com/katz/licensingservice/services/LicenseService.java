package com.katz.licensingservice.services;

import com.katz.licensingservice.config.Config;
import com.katz.licensingservice.model.License;
import com.katz.licensingservice.repository.LicenseRepository;

import java.util.List;
import java.util.UUID;

public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final Config config;

    public LicenseService(LicenseRepository licenseRepository, Config config) {
        this.licenseRepository = licenseRepository;
        this.config = config;
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

}
