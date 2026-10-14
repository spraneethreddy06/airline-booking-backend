
package com.example.payment_service.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    private final String serviceSecret;

    public FeignConfig(
            @Value("${payment.service.secret}") String serviceSecret) {
        this.serviceSecret = serviceSecret;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Always attach the shared secret to internal service calls.
            requestTemplate.header("X-Service-Secret", serviceSecret);

            // Forward the user's JWT when the call originates from an HTTP request.
            if (RequestContextHolder.getRequestAttributes()
                    instanceof ServletRequestAttributes attributes) {

                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && !authHeader.isBlank()) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
