package com.colak.springresilience4jretrytutorial.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private static final String SERVICE_NAME = "order-service";
    private static final String ADDRESS_SERVICE_URL = "http://localhost:9090/addresses/";

    private final RestTemplate restTemplate;

    @Retry(name = SERVICE_NAME, fallbackMethod = "fallbackMethod")
    public String getOrderByPostCode() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ADDRESS_SERVICE_URL,
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();

        } catch (HttpServerErrorException exception) {
            log.info(STR."Retry due to http server error at: \{Instant.now()}");
            throw exception;
        } catch (ResourceAccessException exception) {
            log.info(STR."Retry due to resource access at: \{Instant.now()}");
            throw exception;
        }
    }

    private String fallbackMethod(Exception exception) {
        return "Address service is not responding properly";
    }
}
