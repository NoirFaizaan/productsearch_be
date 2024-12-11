package com.sapient.ProductSearch.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testRestTemplateBean() {
        // Retrieve the RestTemplate bean from the application context
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

        // Assert that the RestTemplate bean is not null
        assertNotNull(restTemplate, "RestTemplate bean should not be null");
    }

    @Test
    public void testExternalApiConfig() {
        // Optionally test if the externalApiConfig bean is properly injected
        ExternalApiConfig externalApiConfig = applicationContext.getBean(ExternalApiConfig.class);
        assertNotNull(externalApiConfig, "ExternalApiConfig bean should not be null");
    }
}
