package com.katz.licensingservice.external;

import com.katz.licensingservice.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {
    private final RestTemplate restTemplate;

    @Autowired
    public OrganizationRestTemplateClient(@Lazy RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Organization getOrganizationById(String organizationId) {
        ResponseEntity<Organization> response = restTemplate.exchange(
                "http://organizationservice/v1/organizations/{organizationId}",
                HttpMethod.GET,
                null,
                Organization.class,
                organizationId
        );

        return response.getBody();
    }
}
