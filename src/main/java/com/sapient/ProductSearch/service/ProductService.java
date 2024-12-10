package com.sapient.ProductSearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sapient.ProductSearch.config.ExternalApiConfig;
import com.sapient.ProductSearch.entity.Meta;
import com.sapient.ProductSearch.entity.Product;
import com.sapient.ProductSearch.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ExternalApiConfig externalApiConfig;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Load products from external dataset to the H2 DB.
     */
    public int loadProductsFromExternalApi() {
        String url = externalApiConfig.getApiUrl();
        List<Product> allProducts = new ArrayList<>();
        List<Product> skippedProducts = new ArrayList<>();
        try {
            logger.info("Fetching data from external API...");
            String response = restTemplate.getForObject(url, String.class);

            // Configure ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode productsNode = mapper.readTree(response).get("products");
            if (productsNode == null || !productsNode.isArray()) {
                throw new RuntimeException("Invalid API response: 'products' field is missing or not an array.");
            }

            List<Product> products = mapper.convertValue(productsNode,
                    mapper.getTypeFactory().constructCollectionType(List.class, Product.class));

            // Set product reference in dimensions, reviews, and meta
            for (Product product : products) {
                boolean productExists = productRepository.findBySku(product.getSku()).isPresent()
                        || productRepository.findById(product.getId()).isPresent();
                if (productExists) {
                    // Log that the product already exists and add it to the skipped list
                    logger.info("Product with SKU/ID {}/{} already exists. Skipping insertion.", product.getSku(),product.getId());
                    skippedProducts.add(product);
                    continue;
                }
                if (product.getDimensions() != null) {
                    product.getDimensions().setProduct(product);
                }
                if (product.getReviews() != null) {
                    product.getReviews().forEach(review -> review.setProduct(product));
                }
                if (product.getMeta() != null) {
                    Meta meta = product.getMeta();
                    if (meta.getCreatedAt() == null) {
                        meta.setCreatedAt(Instant.now());
                    }
                    meta.setUpdatedAt(Instant.now());
                    meta.setProduct(product);
                    
                }
                allProducts.add(product);
            }
            productRepository.saveAll(products); // Save all products with associations
            logger.info("Successfully loaded {} products.", products.size());
            return products.size();
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse JSON response from external API", e);
        } catch (DataAccessException e) {
            logger.error("Database error while saving products: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save products to the database", e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load products due to an unexpected error", e);
        }
    }

}