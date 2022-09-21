package com.katz.organizationservice.service;

import com.katz.organizationservice.model.OrganisationEventEmitter;
import com.katz.organizationservice.model.Organization;
import com.katz.organizationservice.repository.OrganizationRepository;
import com.katz.organizationservice.utils.Event;
import com.katz.organizationservice.utils.UserContextHolder;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.UUID;
import java.util.function.BiFunction;

public class OrganizationService {
    private final OrganizationRepository repository;
    private final StreamBridge bridge;
    private final Tracer tracer;

    public OrganizationService (OrganizationRepository repository, StreamBridge bridge, Tracer tracer) {
        this.repository = repository;
        this.bridge = bridge;
        this.tracer = tracer;
    }

    public Organization findByOrgId(String organizationId) {
        Span span = tracer
                .nextSpan()
                .name("findByOrgId");

        try (Tracer.SpanInScope ws = this.tracer.withSpan(span.start())) {
            return repository.findById(organizationId).orElseGet(Organization::new) ;
        } finally {
            span.tag("peer.service", "postgres");
            span.event("Find by org id");
            span.end();
        }

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
