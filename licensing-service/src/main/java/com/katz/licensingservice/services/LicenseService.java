package com.katz.licensingservice.services;

import com.katz.licensingservice.config.Config;
import com.katz.licensingservice.external.OrganizationDiscoveryClient;
import com.katz.licensingservice.external.OrganizationFeignClient;
import com.katz.licensingservice.external.OrganizationRestTemplateClient;
import com.katz.licensingservice.model.License;
import com.katz.licensingservice.model.Organization;
import com.katz.licensingservice.repository.LicenseRepository;
import com.katz.licensingservice.utils.UserContextHolder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final Config config;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;
    private final OrganizationRestTemplateClient organizationRestTemplateClient;
    private final OrganizationFeignClient organizationFeignClient;
    private static final Logger log = LoggerFactory.getLogger(LicenseService.class);

    public LicenseService(LicenseRepository licenseRepository, Config config, OrganizationDiscoveryClient organizationDiscoveryClient,
                          OrganizationRestTemplateClient organizationRestTemplateClient, OrganizationFeignClient organizationFeignClient) {
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
        this.organizationRestTemplateClient = organizationRestTemplateClient;
        this.organizationFeignClient = organizationFeignClient;
    }

    // Test resource (i.e. database) calls with hystrix
    @HystrixCommand(
            // Defines fallback method to call when the call runs out time
            // Note: the method name should be exactly the same as the method
            // to be invoked by the fallback mechanism
            fallbackMethod = "buildLicensesFallback",
            // Properties to change hystrix default behavior
            commandProperties = {
                    // Property to change waiting time before hystrix could timeout
                    @HystrixProperty(
                            name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "1000"
                    ),
                    ///////////////////////////////////////////////////////////////////////////////////////
                    // Failing fast with hystrix, that is, configuring how many requests in 15ms must fail
                    //////////////////////////////////////////////////////////////////////////////////////

                    // before hystrix can trip the circuit breaker
                    @HystrixProperty(
                            name = "circuitBreaker.requestVolumeThreshold",
                            value = "1"
                    ),
                    // Percentage of failed request, if more or equal we fall back to the fallback method
                    // Calculated after rolling window has passed
                    @HystrixProperty(
                            name = "circuitBreaker.errorThresholdPercentage",
                            value = "50"
                    ),
                    // Time that hystrix should intercept requests and use fallbackmethod before letting calls
                    // through to check if the degrading service is performing better
                    @HystrixProperty(
                            name = "circuitBreaker.sleepWindowInMilliseconds",
                            value = "200000"
                    ),
                    // A window that hystrix will collect requests and check for failures
                    @HystrixProperty(
                            name = "metrics.rollingStats.timeInMilliseconds",
                            value = "15000"
                    ),
                    // The number of times hystrix must collect data in the specified window
                    // In this example will collect 5 times with the period of 3 seconds apart
                    @HystrixProperty(
                            name = "metrics.rollingStats.numBuckets",
                            value = "5"
                    )
            },

            /////////////////////////////////////////////////
            // Implementing bulkhead using hystrix
            //////////////////////////////////////////////////

            threadPoolKey = "licensesByOrgThreadPool",
            threadPoolProperties = {
                    // Setting the number of threads that can handle requests at the same time
                    @HystrixProperty(
                            name = "coreSize",
                            value = "30"
                    ),
                    // Setting the number of requests that can queue as other threads are still
                    // busy with user requests
                    @HystrixProperty(
                            name = "maxQueueSize",
                            value = "10"
                    )
            }
    )
    public List<License> getLicensesByOrgId(String organizationId) {
        randomlySleep();

        log.info(
                "In License service!"
        );

        log.info(
                "User Correlation Id: {}",
                UserContextHolder.get().getCorrelationId()
        );

        return licenseRepository.findByOrganizationId(organizationId);
    }

    public License getLicenseByIdAndOrgId(String licenseId, String organizationId) {
        return licenseRepository.findByIdAndOrganizationId(licenseId, organizationId).withComment(config.getTracerProperty());
    }

    public String saveLicense(License license) {
        license.withId(UUID.randomUUID().toString());
        return licenseRepository.save(license).getId();
    }

    public License getLicenseByIdAndOrgId(String licenseId, String organizationId, String consumerType) {
        License license = licenseRepository.findById(licenseId).get();
        Organization organization = getOrganization(organizationId, consumerType);
        return license
                .withContactName(organization.getContactName())
                .withOrganizationEmail(organization.getContactEmail())
                .withOrganizationName(organization.getName())
                .withOrganizationPhone(organization.getContactPhone());
    }

    // Test remote calls with hystrix (i.e. remote database, microservice producer)
    @HystrixCommand
    private Organization getOrganization(String organizationId, String consumerType) {
        switch (consumerType) {
            case "DiscoveryClient":
                return organizationDiscoveryClient.getOrganization(organizationId);
            case "RestTemplateClient":
                return organizationRestTemplateClient.getOrganizationById(organizationId);
            case "FeignClient":
                return organizationFeignClient.getOrgById(organizationId);
            default:
                return null;
        }
    }

    ////////////////////////////////////////////////////
    ///////         Helper methods to test hystrix
    ///////////////////////////////////////////////////
    private void randomlySleep() {
        Random random = new Random();

        int randInt = random.nextInt((3 - 1) + 1) + 1;

        if (randInt == 3)
            sleep();

    }

    private void sleep() {
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<License> buildLicensesFallback(String organizationId) {
        List<License> fallbackLicense = new ArrayList<>();
        fallbackLicense.add(
                new License()
                        .withId("00000000000-000-000000000")
                        .withProductName("Sorry no licensing information currently available!")
                        .withOrganizationId(organizationId)
        );
        return fallbackLicense;
    }

}
