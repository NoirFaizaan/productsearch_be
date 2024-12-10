package com.sapient.ProductSearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalApiConfig {

    @Value("${external.api.url}")
    private String apiUrl;

    @Value("${external.api.timeout}")
    private int timeout;

    public String getApiUrl() {
        return apiUrl;
    }

    public int getTimeout() {
        return timeout;
    }
}
