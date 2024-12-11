package com.sapient.ProductSearch.service;

import com.sapient.ProductSearch.dto.ProductResponseDTO;
import com.sapient.ProductSearch.entity.Product;
import com.sapient.ProductSearch.exceptions.ProductNotFoundException;
import com.sapient.ProductSearch.repository.ProductRepository;
import com.sapient.ProductSearch.util.ApiResponse;
import com.sapient.ProductSearch.config.ExternalApiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ExternalApiConfig externalApiConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    public void setUp() {
        // Setup a sample product for testing
        product = new Product();
        product.setId(1L);
        product.setTitle("Sample Product");
        product.setDescription("Sample Product Description");
        product.setCategory("Electronics");
        product.setPrice(100.0);
        product.setSku("SKU123");
    }

    @Test
    public void testSearchProducts_ValidQuery_ReturnsProducts() {
        String query = "Sample";

        // Setup product with null reviews
        product.setReviews(null);

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(productRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(productList);

        List<ProductResponseDTO> result = productService.searchProducts(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sample Product", result.get(0).getTitle());
        assertEquals(0, result.get(0).getReviews().size()); // Ensure reviews is an empty list
    }

    @Test
    public void testSearchProducts_QueryTooShort_ThrowsIllegalArgumentException() {
        String query = "ab"; // Length is less than 3

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.searchProducts(query);
        });

        assertEquals("Query must contain at least 3 characters.", exception.getMessage());
    }

    @Test
    public void testSearchProducts_NoResults_ThrowsProductNotFoundException() {
        String query = "NonExistent";

        when(productRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(new ArrayList<>());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.searchProducts(query);
        });

        assertEquals("No products found for the given search query: " + query, exception.getMessage());
    }

    @Test
    public void testGetProductByIdOrSku_ValidIdentifier_ReturnsProduct() {
        String identifier = "SKU123";

        when(productRepository.findByIdOrSku(identifier)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductByIdOrSku(identifier);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    public void testGetProductByIdOrSku_ProductNotFound_ThrowsProductNotFoundException() {
        String identifier = "InvalidSKU";

        when(productRepository.findByIdOrSku(identifier)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductByIdOrSku(identifier);
        });

        assertEquals("Product not found with ID or SKU: " + identifier, exception.getMessage());
    }

    @Test
    public void testLoadProductsFromExternalApi_Success() {
        // Assuming the external API returns a JSON response that is mocked
        String apiUrl = "http://external-api.com/products";
        when(externalApiConfig.getApiUrl()).thenReturn(apiUrl);
        when(restTemplate.getForObject(apiUrl, String.class))
                .thenReturn("{\"products\": [{\"sku\": \"SKU123\", \"title\": \"Sample Product\"}]}");

        ApiResponse response = productService.loadProductsFromExternalApi();

        assertEquals(ApiResponse.Response.SUCCESS, response.getResponse());
        assertTrue(response.getMessage().contains("Added"));
    }

    @Test
    public void testLoadProductsFromExternalApi_ParseError() {
        String apiUrl = "http://external-api.com/products";
        when(externalApiConfig.getApiUrl()).thenReturn(apiUrl);
        when(restTemplate.getForObject(apiUrl, String.class)).thenReturn("{\"products\": [Invalid JSON]}");

        ApiResponse response = productService.loadProductsFromExternalApi();

        assertEquals(ApiResponse.Response.FAILURE, response.getResponse());
        assertTrue(response.getMessage().contains("Failed to parse JSON response"));
    }

    // @Test
    // public void testUpdateProductDetails_UpdatesCorrectly() {
    // Product updatedProduct = new Product();
    // updatedProduct.setTitle("Updated Title");
    // updatedProduct.setDescription("Updated Description");
    // updatedProduct.setPrice(120.0);

    // productService.updateProductDetails(product, updatedProduct);

    // assertEquals("Updated Title", product.getTitle());
    // assertEquals("Updated Description", product.getDescription());
    // assertEquals(120.0, product.getPrice());
    // }
}
