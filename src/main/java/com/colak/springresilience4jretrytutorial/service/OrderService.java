package com.colak.springresilience4jretrytutorial.service;

import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private static final String SERVICE_NAME = "order-service";
    private static final String ADDRESS_SERVICE_URL = "http://localhost:9090/addresses/";

    private final RestTemplate restTemplate;

    private final RetryRegistry retryRegistry;

    // Add Retry Event Listeners
    @PostConstruct
    public void postConstruct() {
        io.github.resilience4j.retry.Retry.EventPublisher eventPublisher = retryRegistry.retry(SERVICE_NAME).getEventPublisher();
        // Activates when any event transpires during the retry process.
        eventPublisher.onEvent(event -> log.info("On Event. Event Details: {}", event));

        // Activates when an error occurs, signifying the failure of the retry attempt.
        eventPublisher.onError(event -> log.info("On Error. Event Details: {}", event));

        // Executes when a retry is attempted.
        eventPublisher.onRetry(event -> log.info("On Retry. Event Details: {}", event));

        //  Initiates when the retry operation achieves success.
        eventPublisher.onSuccess(event -> log.info("On Success. Event Details: {}", event));

        // This takes effect when an error event is disregarded as per the configured Retry settings.
        eventPublisher.onIgnoredError(event -> log.info("On Ignored Error. Event Details: {}", event));

    }

    @Retry(name = SERVICE_NAME, fallbackMethod = "fallbackMethod")
    public String getOrderByPostCode() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                ADDRESS_SERVICE_URL,
                HttpMethod.GET,
                entity,
                String.class);
        return response.getBody();
    }

    private String fallbackMethod(Exception exception) {
        return "Address service is not responding properly";
    }
}
