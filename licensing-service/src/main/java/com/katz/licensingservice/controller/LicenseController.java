package com.katz.licensingservice.controller;

import com.katz.licensingservice.model.License;
import com.katz.licensingservice.services.LicenseService;
import com.katz.licensingservice.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/organization/{organizationId}/licence")
public class LicenseController {

    private final LicenseService licenseService;
    private static final Logger log = LoggerFactory.getLogger(LicenseController.class);

    @Autowired
    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @RequestMapping(
            value = "/{licenseId}",
            method = RequestMethod.GET
    )
    public License getLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId
    ) {
        return licenseService.getLicenseByIdAndOrgId(licenseId, organizationId);
    }

    @RequestMapping(
            method = RequestMethod.GET
    )
    public List<License> getLicensesByOrgId(@PathVariable("organizationId") String organizationId) {

        log.info(
                "User Correlation Id: {}",
                UserContextHolder.get().getCorrelationId()
        );

        return licenseService.getLicensesByOrgId(organizationId);
    }

    @RequestMapping(
            method = RequestMethod.POST
    )
    public String saveLicense(License license) {
        return licenseService.saveLicense(license);
    }

    @RequestMapping(
            value = "/{licenseId}/{consumerType}",
            method = RequestMethod.GET
    )
    public License getLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId,
            @PathVariable("consumerType") String consumerType
    ) {
        return licenseService.getLicenseByIdAndOrgId(licenseId, organizationId, consumerType);
    }
}