package com.sapient.ProductSearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sapient.ProductSearch.config.ExternalApiConfig;
import com.sapient.ProductSearch.dto.DimensionsDTO;
import com.sapient.ProductSearch.dto.MetaDTO;
import com.sapient.ProductSearch.dto.ProductResponseDTO;
import com.sapient.ProductSearch.dto.ReviewDTO;
import com.sapient.ProductSearch.entity.Dimensions;
import com.sapient.ProductSearch.entity.Meta;
import com.sapient.ProductSearch.entity.Product;
import com.sapient.ProductSearch.entity.Review;
import com.sapient.ProductSearch.exceptions.ProductNotFoundException;
import com.sapient.ProductSearch.repository.ProductRepository;
import com.sapient.ProductSearch.util.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * Searches for products by title or description based on the given query.
     */
    public List<ProductResponseDTO> searchProducts(String query) {
        // Ensure that the query has at least 3 characters
        if (query.length() < 3) {
            throw new IllegalArgumentException("Query must contain at least 3 characters.");
        }
        List<Product> products = productRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found for the given search query: " + query);
        }
        List<ProductResponseDTO> productResponseDTOs = new ArrayList<>();
        for (Product product : products) {
            productResponseDTOs.add(convertToDTO(product));
        }

        return productResponseDTOs;
    }

    /**
     * Finds a product by its ID or SKU.
     */
    public Optional<Product> getProductByIdOrSku(String identifier) {
        Optional<Product> product = productRepository.findByIdOrSku(identifier);
        if (product.isEmpty()) {
            throw new ProductNotFoundException("Product not found with ID or SKU: " + identifier);
        }
        return product;
    }

    /**
     * Load products from external dataset to the H2 DB.
     * Updates existing products and adds new products.
     * Returns a summary string of added and updated products.
     */
    public ApiResponse loadProductsFromExternalApi() {
        String url = externalApiConfig.getApiUrl();
        List<Product> addedProducts = new ArrayList<>();
        List<Product> updatedProducts = new ArrayList<>();

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

            // Process each product
            for (Product product : products) {
                Optional<Product> existingProductOpt = productRepository.findBySku(product.getSku());

                if (existingProductOpt.isPresent()) {
                    // Update existing product
                    Product existingProduct = existingProductOpt.get();
                    updateProductDetails(existingProduct, product);
                    updatedProducts.add(existingProduct);
                } else {
                    // Set product references for new product
                    prepareNewProductReferences(product);
                    addedProducts.add(product);
                }
            }

            // Save updated and new products
            productRepository.saveAll(addedProducts);
            productRepository.saveAll(updatedProducts);

            logger.info("Added {} new products.", addedProducts.size());
            logger.info("Updated {} existing products.", updatedProducts.size());

            String message = String.format("Added %d products and updated %d products.", addedProducts.size(), updatedProducts.size());
            logger.info(message);
            return new ApiResponse(ApiResponse.Response.SUCCESS, message,null);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response: {}", e.getMessage(), e);
            return new ApiResponse(ApiResponse.Response.FAILURE, "Failed to parse JSON response from external API",e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            return new ApiResponse(ApiResponse.Response.FAILURE, "Failed to load products due to an unexpected error",e.getMessage());
        }
    }

    /**
     * Update the details of an existing product with new data.
     */
    private void updateProductDetails(Product existingProduct, Product newProduct) {
        existingProduct.setTitle(newProduct.getTitle());
        existingProduct.setDescription(newProduct.getDescription());
        existingProduct.setCategory(newProduct.getCategory());
        existingProduct.setPrice(newProduct.getPrice());
        existingProduct.setDiscountPercentage(newProduct.getDiscountPercentage());
        existingProduct.setRating(newProduct.getRating());
        existingProduct.setStock(newProduct.getStock());
        existingProduct.setBrand(newProduct.getBrand());
        existingProduct.setWeight(newProduct.getWeight());
        existingProduct.setWarrantyInformation(newProduct.getWarrantyInformation());
        existingProduct.setShippingInformation(newProduct.getShippingInformation());
        existingProduct.setAvailabilityStatus(newProduct.getAvailabilityStatus());
        existingProduct.setReturnPolicy(newProduct.getReturnPolicy());
        existingProduct.setMinimumOrderQuantity(newProduct.getMinimumOrderQuantity());
        existingProduct.setThumbnail(newProduct.getThumbnail());
        existingProduct.setTags(newProduct.getTags());
        existingProduct.setImages(newProduct.getImages());

        if (existingProduct.getMeta() == null) {
            existingProduct.setMeta(new Meta());
        }
        existingProduct.getMeta().setUpdatedAt(Instant.now());
    }

    /**
     * Prepare references for a new product.
     */
    private void prepareNewProductReferences(Product product) {
        if (product.getDimensions() != null) {
            product.getDimensions().setProduct(product);
        }
        if (product.getReviews() != null) {
            product.getReviews().forEach(review -> review.setProduct(product));
        }
        if (product.getMeta() == null) {
            product.setMeta(new Meta());
        }
        product.getMeta().setCreatedAt(Instant.now());
        product.getMeta().setUpdatedAt(Instant.now());
        product.getMeta().setProduct(product);
    }

    private ProductResponseDTO convertToDTO(Product product) {
   ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setRating(product.getRating());
        dto.setStock(product.getStock());
        dto.setBrand(product.getBrand());
        dto.setSku(product.getSku());
        dto.setWeight(product.getWeight());
        dto.setWarrantyInformation(product.getWarrantyInformation());
        dto.setShippingInformation(product.getShippingInformation());
        dto.setAvailabilityStatus(product.getAvailabilityStatus());
        dto.setTags(product.getTags());
        dto.setReviews(convertToReviewDTOs(product.getReviews()));  // Convert reviews to DTOs
        dto.setDimensions(convertToDimensionsDTO(product.getDimensions()));  // Convert dimensions to DTO
        dto.setMeta(convertToMetaDTO(product.getMeta()));  // Convert meta to DTO
        dto.setReturnPolicy(product.getReturnPolicy());
        dto.setMinimumOrderQuantity(product.getMinimumOrderQuantity());
        dto.setThumbnail(product.getThumbnail());
        dto.setImages(product.getImages());

        return dto;
    }

    /**
     * Convert a list of Review entities to ReviewDTOs
     */
    private List<ReviewDTO> convertToReviewDTOs(List<Review> reviews) {
        if (reviews == null) {
            return new ArrayList<>();  // Return an empty list if reviews is null
        }
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews) {
            reviewDTOs.add(new ReviewDTO(
                    review.getRating(),
                    review.getComment(),
                    review.getDate(),
                    review.getReviewerName(),
                    review.getReviewerEmail()
            ));
        }
        return reviewDTOs;
    }

    /**
     * Convert Dimensions entity to DimensionsDTO
     */
    private DimensionsDTO convertToDimensionsDTO(Dimensions dimensions) {
        if (dimensions == null) {
            return null;
        }
        return new DimensionsDTO(
                dimensions.getWidth(),
                dimensions.getHeight(),
                dimensions.getDepth()
        );
    }

    /**
     * Convert Meta entity to MetaDTO
     */
    private MetaDTO convertToMetaDTO(Meta meta) {
        if (meta == null) {
            return null;
        }
        return new MetaDTO(
                meta.getBarcode(),
                meta.getQrCode(),
                meta.getCreatedAt(),
                meta.getUpdatedAt()
        );
    }

}
