package com.katz.organizationservice.service;

import com.katz.organizationservice.model.Organization;
import com.katz.organizationservice.repository.OrganizationRepository;

import java.util.UUID;

public class OrganizationService {
    private final OrganizationRepository repository;

    public OrganizationService (OrganizationRepository repository) {
        this.repository = repository;
    }

    public Organization findByOrgId(String organizationId) {
        return repository.findById(organizationId).get();
    }

    public Organization addOrUpdateOrganization(Organization organization) {
        if (organization.getId() == null)
            organization.setId(UUID.randomUUID().toString());

        return repository.save(organization);
    }
}
