package com.sapient.ProductSearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sapient.ProductSearch.dto.ProductResponseDTO;
import com.sapient.ProductSearch.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds products containing the given keyword in either title or description.
     */
    List<Product> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleKeyword, String descriptionKeyword);

    /**
     * Finds a product by its SKU.
     */
    Optional<Product> findBySku(String sku);

    /**
     * Finds a product by its ID or SKU.
     */
    default Optional<Product> findByIdOrSku(String identifier) {
        if (identifier.matches("\\d+")) {
            return findById(Long.valueOf(identifier));
        }
        return findBySku(identifier);
    }
}
