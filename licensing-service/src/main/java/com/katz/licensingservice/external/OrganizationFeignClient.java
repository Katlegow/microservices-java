package com.katz.licensingservice.external;

import com.katz.licensingservice.model.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@FeignClient("organizationservice")
public interface OrganizationFeignClient {

    @RequestMapping(
            value = "/v1/organizations/{organizationId}",
            method = RequestMethod.GET,
            consumes = APPLICATION_JSON
    )
    Organization getOrgById(@PathVariable("organizationId") String organizationId);
}
