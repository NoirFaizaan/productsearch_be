package com.sapient.ProductSearch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sapient.ProductSearch.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * Orchestration API to load products into in-memory DB.
     */
    @PostMapping("/load")
    public ResponseEntity<String> loadProducts() {
        logger.info("Loading products from external dataset...");
        int count = productService.loadProductsFromExternalApi();
        logger.info("Successfully loaded {} products into the database.", count);
        return ResponseEntity.status(HttpStatus.OK).body("Loaded " + count + " products into the database.");
    }
}
