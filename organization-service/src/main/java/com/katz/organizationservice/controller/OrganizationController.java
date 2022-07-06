package com.katz.organizationservice.controller;

import com.katz.organizationservice.model.Organization;
import com.katz.organizationservice.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/organizations")
@RestController
public class OrganizationController {
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
