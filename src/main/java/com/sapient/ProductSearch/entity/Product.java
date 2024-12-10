package com.sapient.ProductSearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @NotNull(message = "Product ID cannot be null")
    @Min(value = 1, message = "Product ID must be greater than 0")
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Discount percentage cannot be null")
    @DecimalMin(value = "0.0", message = "Discount percentage must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Discount percentage must be less than or equal to 100")
    private Double discountPercentage;

    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private Double rating;

    @NotNull(message = "Stock cannot be null")
    @Min(value = 0, message = "Stock must be a non-negative integer")
    private Integer stock;

    // @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @NotBlank(message = "SKU cannot be blank")
    private String sku;

    @NotNull(message = "Weight cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0")
    private Double weight;

    @NotBlank(message = "Warranty information cannot be blank")
    private String warrantyInformation;

    @NotBlank(message = "Shipping information cannot be blank")
    private String shippingInformation;

    @NotBlank(message = "Availability status cannot be blank")
    private String availabilityStatus;

    @NotBlank(message = "Return policy cannot be blank")
    private String returnPolicy;

    @NotNull(message = "Minimum order quantity cannot be null")
    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minimumOrderQuantity;

    @NotBlank(message = "Thumbnail URL cannot be blank")
    @Pattern(regexp = "^(https?://.*)$", message = "Thumbnail must be a valid URL")
    private String thumbnail;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Dimensions dimensions;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Meta meta;

    @ElementCollection
    private List<String> tags; 

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @ElementCollection
    private List<String> images;
}