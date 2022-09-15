package com.katz.licensingservice.external;

import com.katz.licensingservice.cache.OrganizationRedisOperation;
import com.katz.licensingservice.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {
    Logger log = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);
    private final RestTemplate restTemplate;
    private final OrganizationRedisOperation organizationRedisOperation;

    @Autowired
    public OrganizationRestTemplateClient(@Lazy RestTemplate restTemplate, @Lazy OrganizationRedisOperation organizationRedisOperation) {
        this.restTemplate = restTemplate;
        this.organizationRedisOperation = organizationRedisOperation;
    }

    public Organization getOrganizationById(String organizationId) {

        Organization org = checkCache(organizationId);

        if (org != null) {
            log.info("Successfully retrieved organization {} from redis", organizationId);
            return org;
        }

        log.info("Could not find organization {} in redis", organizationId);

        ResponseEntity<Organization> response = restTemplate.exchange(
                "http://organizationservice/v1/organizations/{organizationId}",
                HttpMethod.GET,
                null,
                Organization.class,
                organizationId
        );

        org = response.getBody();

        if (org != null)
            cache(org);

        return org;
    }

    private Organization checkCache(String organizationId) {
        try {
            return organizationRedisOperation.findOrganization(organizationId);
        } catch (Exception ex) {
            log.warn("Could not contact redis server: {}",ex);
            return null;
        }
    }

    public void cache(Organization organization) {
        try {
            log.info("Caching organization {}", organization.getId());
            organizationRedisOperation.saveOrganization(organization);
            log.info("Successfully cached organization {}", organization.getId());
        } catch (Exception ex) {
            log.warn(
                    "Could not cache organization {}: {}",
                    organization.getId(),
                    ex
            );
        }
    }
}
