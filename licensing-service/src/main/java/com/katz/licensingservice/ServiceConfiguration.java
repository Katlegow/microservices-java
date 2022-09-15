package com.katz.licensingservice;

import com.katz.licensingservice.config.Config;
import com.katz.licensingservice.external.OrganizationDiscoveryClient;
import com.katz.licensingservice.external.OrganizationFeignClient;
import com.katz.licensingservice.external.OrganizationRestTemplateClient;
import com.katz.licensingservice.interceptors.RestTemplateRequestInterceptor;
import com.katz.licensingservice.repository.LicenseRepository;
import com.katz.licensingservice.services.LicenseService;
import com.katz.licensingservice.utils.UserContextPropagator;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4jBulkheadConfigurationBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4jBulkheadProvider;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Configuration
@SuppressWarnings("rawtypes")
public class ServiceConfiguration {

    private final LicenseRepository licenseRepository;
    private final Config config;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;
    private final OrganizationRestTemplateClient organizationRestTemplateClient;
    private final OrganizationFeignClient organizationFeignClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public ServiceConfiguration(LicenseRepository licenseRepository, Config config, OrganizationDiscoveryClient organizationDiscoveryClient,
                                OrganizationRestTemplateClient organizationRestTemplateClient, OrganizationFeignClient organizationFeignClient,
                                @Lazy CircuitBreakerFactory circuitBreakerFactory) {
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
        this.organizationRestTemplateClient = organizationRestTemplateClient;
        this.organizationFeignClient = organizationFeignClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Bean
    public LicenseService getLicenseService() {
        return new LicenseService(licenseRepository, config, organizationDiscoveryClient, organizationRestTemplateClient,
                organizationFeignClient, circuitBreakerFactory);
    }


    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate template = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();

        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new RestTemplateRequestInterceptor()));
        } else {
            interceptors.add( new RestTemplateRequestInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(
                id -> new Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(1)).build())
                        .circuitBreakerConfig(
                                CircuitBreakerConfig
                                        .custom()
                                          .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                                          .slidingWindowSize(15)
                                          .minimumNumberOfCalls(20)
                                          .failureRateThreshold(60.0f)
                                        .waitDurationInOpenState(Duration.ofSeconds(15))
                                        .permittedNumberOfCallsInHalfOpenState(5)
                                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                                        .slowCallRateThreshold(60.0f)
                                        .build())
                        .build());
    }

    @Bean
    public Customizer<Resilience4jBulkheadProvider> defaultBulkheadCustomizer() {
        return provider -> {
            provider.configureDefault(
                    id -> new Resilience4jBulkheadConfigurationBuilder()
                            .bulkheadConfig(
                                    BulkheadConfig.custom()
                                            .maxConcurrentCalls(4)
                                            .build()
                            ).threadPoolBulkheadConfig(
                                    ThreadPoolBulkheadConfig.custom()
                                            .contextPropagator(new UserContextPropagator())
                                            .coreThreadPoolSize(2)
                                            .maxThreadPoolSize(4).build()
                            ).build()
            );
        };
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

}
