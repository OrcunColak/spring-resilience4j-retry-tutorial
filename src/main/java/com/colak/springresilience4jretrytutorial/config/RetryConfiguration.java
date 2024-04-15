package com.colak.springresilience4jretrytutorial.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration {

    private final RetryRegistry retryRegistry;

    // Example that shows how to build a bean using API instead of yaml configuration
    @Bean
    public Retry getCustomerDetailsByCustomerIdRetryConfig() {
        RetryConfig customerDetailsRetryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(HttpClientErrorException.class, HttpServerErrorException.class)
                // .ignoreExceptions(DataNotFoundException.class)
                .build();

        return retryRegistry.retry("getCustomerDetailsByCustomerId", customerDetailsRetryConfig);
    }

}
