package com.katz.licensingservice.cache;

import com.katz.licensingservice.model.Organization;

public interface OrganizationRedisOperation {
    void saveOrganization(Organization org);
    void updateOrganization(Organization org);
    void deleteOrganization(String organizationId);
    Organization findOrganization(String organizationId);
}
