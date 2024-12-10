package com.sapient.ProductSearch.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sapient.ProductSearch.dto.ProductResponseDTO;
import com.sapient.ProductSearch.entity.Product;
import com.sapient.ProductSearch.exceptions.InvalidInputException;
import com.sapient.ProductSearch.exceptions.ProductNotFoundException;
import com.sapient.ProductSearch.service.ProductService;
import com.sapient.ProductSearch.util.ApiResponse;

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
    public ResponseEntity<ApiResponse> loadProducts() {
        logger.info("Loading products from external dataset...");
        try {
            ApiResponse response = productService.loadProductsFromExternalApi();
            logger.info("Product loading completed: {}", response.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error occurred while loading products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(ApiResponse.Response.FAILURE, "Failed to load products", null));
        }
    }

    /**
     * Search for products based on title or description.
     * The user can enter the first 3 characters of the title or description.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProducts(@RequestParam String query) {
        if (query == null || query.length() < 3) {
            throw new InvalidInputException("Query must contain at least 3 characters.");
        }
        // Optional: Add regex to filter out special characters or unwanted input
        if (!query.matches("[a-zA-Z0-9 ]+")) {
            throw new InvalidInputException("Query contains invalid characters.");
        }

        logger.info("Searching for products with query: {}", query);
        List<ProductResponseDTO> products = productService.searchProducts(query);
        return ResponseEntity.ok(new ApiResponse(ApiResponse.Response.SUCCESS, "Products found", products));
    }

    /**
     * Fetch a specific product by its ID or SKU.
     */
    @GetMapping("/{idOrSku}")
    public ResponseEntity<ApiResponse> getProductByIdOrSku(@PathVariable String idOrSku) {
        // Check if the ID is a number or SKU format (for simplicity, let's assume SKU
        // is alphanumeric)
        if (!idOrSku.matches("\\d+")) { // ID should be numeric
            if (!idOrSku.matches("[a-zA-Z0-9]+")) { // SKU should be alphanumeric
                throw new InvalidInputException("Invalid ID or SKU format.");
            }
        }

        logger.info("Fetching product with ID or SKU: {}", idOrSku);
        return productService.getProductByIdOrSku(idOrSku)
                .map(product -> ResponseEntity
                        .ok(new ApiResponse(ApiResponse.Response.SUCCESS, "Product found", product)))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID or SKU: " + idOrSku));
    }
}