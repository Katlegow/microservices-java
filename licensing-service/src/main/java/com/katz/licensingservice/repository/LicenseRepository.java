package com.katz.licensingservice.repository;

import com.katz.licensingservice.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<License, String> {

    List<License> findByOrganizationId(String organizationId);
    License findByIdAndOrganizationId(String licenseId, String organizationId);
}
