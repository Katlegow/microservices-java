package com.katz.licensingservice.external;

import com.katz.licensingservice.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OrganizationDiscoveryClient {
    private final DiscoveryClient discoveryClient;

    @Autowired
    public OrganizationDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public Organization getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();

        List<ServiceInstance> instances = discoveryClient.getInstances("organizationservice");

        if (instances.isEmpty())
            return null;

        String serviceUri = String.format(
                "%s/v1/organizations/%s",
                instances.get(0).getUri().toString(),
                organizationId
        );

        ResponseEntity<Organization> response = restTemplate.exchange(serviceUri, HttpMethod.GET, null, Organization.class);

        return response.getBody();
    }
}
