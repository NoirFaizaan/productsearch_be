package com.sapient.ProductSearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sapient.ProductSearch.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds products containing the given keyword in either title or description.
     * 
     * @param titleKeyword       Keyword to search in the product title.
     * @param descriptionKeyword Keyword to search in the product description.
     * @return List of matching products.
     */
    List<Product> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleKeyword, String descriptionKeyword);

    /**
     * Finds a product by its SKU.
     * 
     * @param sku The SKU of the product.
     * @return Optional containing the product if found.
     */
    Optional<Product> findBySku(String sku);

    /**
     * Finds a product by its ID or SKU.
     * If the identifier is numeric, it searches by ID.
     * Otherwise, it searches by SKU.
     * 
     * @param identifier Numeric ID or alphanumeric SKU.
     * @return Optional containing the product if found.
     */
    default Optional<Product> findByIdOrSku(String identifier) {
        if (identifier.matches("\\d+")) {
            return findById(Long.valueOf(identifier));
        }
        return findBySku(identifier);
    }
}
