package com.katz.organizationservice.service;

import com.katz.organizationservice.model.OrganisationEventEmitter;
import com.katz.organizationservice.model.Organization;
import com.katz.organizationservice.repository.OrganizationRepository;
import com.katz.organizationservice.utils.Event;
import com.katz.organizationservice.utils.UserContextHolder;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.UUID;
import java.util.function.BiFunction;

public class OrganizationService {
    private final OrganizationRepository repository;
    private final StreamBridge bridge;

    public OrganizationService (OrganizationRepository repository, StreamBridge bridge) {
        this.repository = repository;
        this.bridge = bridge;
    }

    public Organization findByOrgId(String organizationId) {
        return repository.findById(organizationId).orElseGet(Organization::new) ;
    }

    public Organization addOrUpdateOrganization(Organization organization) {
        Event action = Event.UPDATE;
        if (organization.getId() == null) {
            organization.setId(UUID.randomUUID().toString());
            action = Event.ADD;
        }

        Organization org = repository.save(organization);

        bridge.send(
                "bridgeOrganizationChangeEvent-out-0",
                organizationChangeEvent(String.valueOf(action), org.getId())
        );

        return org;
    }

    public void deleteOrganization(String organizationId) {
        repository.deleteById(organizationId);
        bridge.send(
                "bridgeOrganizationChangeEvent-out-0",
                organizationChangeEvent(String.valueOf(Event.DELETE), organizationId)
        );
    }

    private OrganisationEventEmitter organizationChangeEvent(String action, String orgId) {

        return new OrganisationEventEmitter(
                OrganisationEventEmitter.class.getTypeName(),
                UserContextHolder.get().getCorrelationId(),
                orgId,
                action
        );
    }
}
