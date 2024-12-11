package com.sapient.ProductSearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapient.ProductSearch.dto.ProductResponseDTO;
import com.sapient.ProductSearch.entity.Product;
import com.sapient.ProductSearch.service.ProductService;
import com.sapient.ProductSearch.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ApiResponse successResponse;
    private ApiResponse failureResponse;

    @BeforeEach
    public void setup() {
        successResponse = new ApiResponse(ApiResponse.Response.SUCCESS, "Products found",
                Arrays.asList(new ProductResponseDTO()));
        failureResponse = new ApiResponse(ApiResponse.Response.FAILURE, "Failed to load products", null);
    }

    @Test
    public void testLoadProducts_Success() throws Exception {
        when(productService.loadProductsFromExternalApi()).thenReturn(successResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/load"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Products found"));
    }

    @Test
    public void testLoadProducts_Failure() throws Exception {
        // Mocking the service to throw an exception
        when(productService.loadProductsFromExternalApi()).thenThrow(new RuntimeException("Error loading products"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/load"))
                .andExpect(status().isInternalServerError()) // Ensure that your controller returns 500 for failures
                .andExpect(jsonPath("$.response").value("FAILURE"))
                .andExpect(jsonPath("$.message").value("Failed to load products"));
    }

    @Test
    public void testSearchProducts_Success() throws Exception {
        String query = "product";
        when(productService.searchProducts(query)).thenReturn(Arrays.asList(new ProductResponseDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Products found"));
    }

    @Test
    public void testSearchProducts_QueryTooShort() throws Exception {
        String query = "ab"; // Invalid query (less than 3 characters)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                .param("query", query))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input: Query must contain at least 3 characters."));
    }

    @Test
    public void testSearchProducts_InvalidCharacters() throws Exception {
        String query = "product$"; // Invalid characters in query

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                .param("query", query))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input: Query contains invalid characters."));
    }

    @Test
    public void testGetProductByIdOrSku_Success() throws Exception {
        String idOrSku = "123";

        // Mock a Product object to return from the service
        Product product = new Product();
        product.setId(123L);
        product.setTitle("Sample Product");
        // Add other necessary fields to the product object

        // Mock the service to return an Optional<Product> when getProductByIdOrSku is
        // called
        when(productService.getProductByIdOrSku(idOrSku)).thenReturn(Optional.of(product));

        // Perform the GET request to fetch the product by ID or SKU
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{idOrSku}", idOrSku))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Product found"))
                .andReturn();

        // You can also validate the returned product's properties
        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Sample Product")); // Assuming the product title is present in the response
    }

    @Test
    public void testGetProductByIdOrSku_ProductNotFound() throws Exception {
        String idOrSku = "123";
        when(productService.getProductByIdOrSku(idOrSku)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{idOrSku}", idOrSku))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with ID or SKU: " + idOrSku));
    }

    @Test
    public void testGetProductByIdOrSku_InvalidFormat() throws Exception {
        String idOrSku = "invalid-sku";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{idOrSku}", idOrSku))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input: Invalid ID or SKU format."));
    }
}
