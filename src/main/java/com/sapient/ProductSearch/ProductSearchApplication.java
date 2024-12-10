package com.sapient.ProductSearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Admin Service API", version = "1.0", description = "Admin Service for managing providers and meters"))

public class ProductSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductSearchApplication.class, args);
	}

}
