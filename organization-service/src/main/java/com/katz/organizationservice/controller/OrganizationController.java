package com.katz.organizationservice.controller;

import com.katz.organizationservice.model.Organization;
import com.katz.organizationservice.service.OrganizationService;
import com.katz.organizationservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/organizations")
@RestController
public class OrganizationController {
    Logger log = LoggerFactory.getLogger(OrganizationController.class);
    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController (OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @RequestMapping(
            value = "/{organizationId}",
            method = RequestMethod.GET
    )
    public Organization getOrgById(@PathVariable("organizationId") String organizationId) {
        log.info(
                "Processing request|Correlation ID: {}",
                UserContextHolder.get().getCorrelationId()
        );
        return organizationService.findByOrgId(organizationId);
    }

    @RequestMapping(
            method = RequestMethod.POST
    )
    public Organization add(Organization organization) {
        return organizationService.addOrUpdateOrganization(organization);
    }

    @RequestMapping(
            method = RequestMethod.PUT
    )
    public Organization update(Organization organization) {
        return organizationService.addOrUpdateOrganization(organization);
    }
}
