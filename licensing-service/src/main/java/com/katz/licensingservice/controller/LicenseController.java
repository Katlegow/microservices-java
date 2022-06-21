package com.katz.licensingservice.controller;

import com.katz.licensingservice.model.License;
import com.katz.licensingservice.services.LicenseService;
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
        return licenseService.getLicensesByOrgId(organizationId);
    }

    @RequestMapping(
            method = RequestMethod.POST
    )
    public String saveLicense(License license) {
        return licenseService.saveLicense(license);
    }
}