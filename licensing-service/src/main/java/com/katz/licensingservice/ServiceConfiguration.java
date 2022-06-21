package com.katz.licensingservice;

import com.katz.licensingservice.config.Config;
import com.katz.licensingservice.repository.LicenseRepository;
import com.katz.licensingservice.services.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    private final LicenseRepository licenseRepository;
    private final Config config;

    @Autowired
    public ServiceConfiguration(LicenseRepository licenseRepository,Config config) {
        this.licenseRepository = licenseRepository;
        this.config = config;
    }

    @Bean
    public LicenseService getLicenseService() {
        return new LicenseService(licenseRepository, config);
    }

}
